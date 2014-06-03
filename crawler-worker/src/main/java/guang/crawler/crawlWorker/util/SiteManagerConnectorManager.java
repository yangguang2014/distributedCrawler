package guang.crawler.crawlWorker.util;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.siteManagers.SiteManagerInfo;
import guang.crawler.commons.WebURL;
import guang.crawler.connector.JSONServerConnector;
import guang.crawler.jsonServer.DataPacket;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.alibaba.fastjson.JSON;

public class SiteManagerConnectorManager implements Watcher
{
	private HashMap<String, JSONServerConnector>	     connectors;
	private CenterConfig	                             centerConfig;
	private Iterator<Entry<String, JSONServerConnector>>	connectorIterator;
	
	private Date	                                     eventTime	    = new Date();
	private Date	                                     lastUpdateTime	= new Date();
	
	public SiteManagerConnectorManager(CenterConfig controller)
	        throws UnknownHostException, IOException
	{
		this.centerConfig = controller;
		this.connectors = new HashMap<String, JSONServerConnector>();
	}
	
	public void exit() throws IOException
	{
		this.connectorIterator = this.connectors.entrySet().iterator();
		while (this.connectorIterator.hasNext())
		{
			this.exit(this.connectorIterator.next().getValue());
		}
		
	}
	
	public void exit(JSONServerConnector connector) throws IOException
	{
		connector.send(DataPacket.EXIT_DATA_PACKET);
		
	}
	
	public int getSiteManagerConnectorSize()
	{
		return this.connectors.size();
	}
	
	public WebURL getURL() throws IOException, InterruptedException,
	        KeeperException
	{
		// 如果有新的情况发生，就更新站点信息
		if (this.lastUpdateTime.before(this.eventTime))
		{
			this.refreshConnectors();
		}
		DataPacket data = new DataPacket("/url/get", null, null);
		HashMap<String, String> requestData = new HashMap<String, String>();
		requestData.put("COUNT", "1");
		data.setData(requestData);
		if (this.connectors.size() == 0)
		{
			this.refreshConnectors();
		}
		if (this.connectors.size() == 0)
		{
			return null;
		}
		if ((this.connectorIterator == null)
		        || !this.connectorIterator.hasNext())
		{
			this.connectorIterator = this.connectors.entrySet().iterator();
		}
		
		for (int j = 0; j < this.connectors.size(); j++)
		{
			if (!this.connectorIterator.hasNext())
			{
				this.connectorIterator = this.connectors.entrySet().iterator();
			}
			JSONServerConnector connector = this.connectorIterator.next()
			        .getValue();
			DataPacket result = null;
			try
			{
				connector.send(data);
				result = connector.read();
			} catch (IOException e)
			{
				this.refreshConnectors();
			}
			if (result != null)
			{
				int count = Integer.parseInt(result.getData().get("COUNT"));
				if (count > 0)
				{
					String url = result.getData().get("URL_LIST" + 0);
					return JSON.parseObject(url, WebURL.class);
				}
			}
			
		}
		return null;
	}
	
	@Override
	public void process(WatchedEvent event)
	{
		// 首先继续注册事件监听器
		try
		{
			CenterConfig.me().getWorkersInfo().getOnlineWorkers()
			        .getWorkerRefreshPath().watch(this);
		} catch (KeeperException e)
		{
			e.printStackTrace();
			return;
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			return;
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		if (EventType.NodeDataChanged == event.getType())
		{
			synchronized (this.eventTime)
			{
				this.eventTime.setTime(System.currentTimeMillis());
				this.eventTime.notifyAll();
			}
		}
	}
	
	public void putData(WebURL parent, List<WebURL> outGoings)
	        throws IOException
	{
		DataPacket request = new DataPacket();
		request.setTitle("/url/put");
		HashMap<String, String> requestData = new HashMap<String, String>();
		requestData.put("PARENT", JSON.toJSONString(parent));
		int size = 0;
		if (outGoings != null)
		{
			size = outGoings.size();
		}
		
		int sendSize = 0;
		if (size > 0)
		{
			for (WebURL url : outGoings)
			{
				requestData.put("URL" + sendSize++, JSON.toJSONString(url));
			}
		}
		requestData.put("COUNT", String.valueOf(sendSize));
		request.setData(requestData);
		JSONServerConnector connector = this.connectors.get(parent
		        .getSiteManagerId());
		connector.send(request);
		
	}
	
	/**
	 * 更新连接器，更新到最新的事件发生的状态
	 * 
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws IOException
	 */
	public void refreshConnectors() throws InterruptedException,
	        KeeperException, IOException
	{
		while (true)
		{
			long now = System.currentTimeMillis();
			Set<String> keys = this.connectors.keySet();
			for (String key : keys)
			{
				try
				{
					JSONServerConnector connector = this.connectors.get(key);
					this.exit(connector);
					connector.shutdown();
				} catch (IOException e)
				{
					// Skit it
					e.printStackTrace();
				}
			}
			this.connectors.clear();
			List<SiteManagerInfo> dispatchedSiteManagers = this.centerConfig
			        .getSiteManagersConfigInfo().getOnlineSiteManagers()
			        .getAllDispatchedSiteManagers();
			if ((dispatchedSiteManagers != null)
			        && (dispatchedSiteManagers.size() > 0))
			{
				for (SiteManagerInfo siteManagerInfo : dispatchedSiteManagers)
				{
					String siteManagerAddress = siteManagerInfo
					        .getManagerAddress();
					if (siteManagerAddress != null)
					{
						String[] addInfo = siteManagerAddress.split(":");
						JSONServerConnector connector;
						try
						{
							connector = new JSONServerConnector(addInfo[0],
							        Integer.parseInt(addInfo[1]));
							this.connectors.put(
							        siteManagerInfo.getSiteManagerId(),
							        connector);
						} catch (NumberFormatException e)
						{
							// Skip it
							e.printStackTrace();
						} catch (IOException e)
						{
							// Skip it
							e.printStackTrace();
						}
						
					}
				}
			}
			this.lastUpdateTime.setTime(now);
			synchronized (this.eventTime)
			{
				if (this.lastUpdateTime.after(this.eventTime))
				{
					break;
				}
			}
			
		}
		
	}
}
