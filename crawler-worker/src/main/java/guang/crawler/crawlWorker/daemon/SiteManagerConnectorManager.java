package guang.crawler.crawlWorker.daemon;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.siteManagers.SiteManagerInfo;
import guang.crawler.commons.WebURL;
import guang.crawler.connector.JSONServerConnector;
import guang.crawler.jsonServer.DataPacket;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.alibaba.fastjson.JSON;

public class SiteManagerConnectorManager implements Watcher, Runnable
{
	private static SiteManagerConnectorManager	connectorManager;
	
	public static SiteManagerConnectorManager me()
	{
		if (SiteManagerConnectorManager.connectorManager == null)
		{
			SiteManagerConnectorManager.connectorManager = new SiteManagerConnectorManager();
		}
		return SiteManagerConnectorManager.connectorManager;
	}
	
	private HashMap<String, JSONServerConnector>	     connectors;
	private CenterConfig	                             centerConfig;
	private Iterator<Entry<String, JSONServerConnector>>	connectorIterator;
	
	private Date	                                     eventTime	= new Date();
	
	private Thread	                                     managerThread;
	
	private SiteManagerConnectorManager()
	{
		this.centerConfig = CenterConfig.me();
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
	
	/**
	 * 获取一个URL
	 * 
	 * @return 返回null，表示获取失败，应当再次获取；返回非null表示成功获取了URL；如果当前没有可以使用的连接，那么直接阻塞。
	 * @throws InterruptedException
	 */
	public WebURL getURL() throws InterruptedException
	{
		JSONServerConnector connector = null;
		synchronized (this.connectors)
		{
			while (this.connectors.size() == 0)
			{
				this.connectors.wait();
			}
			if ((this.connectorIterator == null)
			        || !this.connectorIterator.hasNext())
			{
				this.connectorIterator = this.connectors.entrySet().iterator();
			}
			connector = this.connectorIterator.next().getValue();
		}
		if (connector == null)
		{
			return null;
		}
		DataPacket data = new DataPacket("/url/get", null, null);
		HashMap<String, String> requestData = new HashMap<String, String>();
		requestData.put("COUNT", "1");
		data.setData(requestData);
		DataPacket result = null;
		try
		{
			connector.send(data);
			result = connector.read();
		} catch (IOException e)
		{
			result = null;
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
		return null;
	}
	
	public SiteManagerConnectorManager init()
	{
		this.managerThread = new Thread(this, "SiteManagerConnectorDaemon");
		this.managerThread.setDaemon(true);
		return this;
	}
	
	@Override
	public void process(WatchedEvent event)
	{
		// 首先继续注册事件监听器
		try
		{
			CenterConfig.me().getWorkersInfo().getOnlineWorkers()
			        .getWorkerRefreshPath().watch(this);
		} catch (Exception e)
		{
			return;
		}
		synchronized (this.eventTime)
		{
			this.eventTime.setTime(System.currentTimeMillis());
			this.eventTime.notifyAll();
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
		synchronized (this.connectors)
		{
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
						} catch (IOException e)
						{
							// Skip it
						}
						
					}
				}
			}
			this.connectors.notifyAll();
			
		}
		
	}
	
	@Override
	public void run()
	{
		try
		{
			this.watch();
		} catch (Exception e)
		{
			return;
		}
		while (true)
		{
			Date now = new Date();
			try
			{
				// 更新所有的连接
				this.refreshConnectors();
			} catch (InterruptedException e)
			{
				return;
			} catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					Thread.sleep(1000);
					continue;
				} catch (InterruptedException e1)
				{
					return;
				}
			}
			// 最后检查一下是否需要继续更新
			synchronized (this.eventTime)
			{
				if (now.after(this.eventTime))
				{
					try
					{
						this.eventTime.wait();
					} catch (InterruptedException e)
					{
						return;
					}
				}
			}
		}
	}
	
	public void start()
	{
		if (this.managerThread != null)
		{
			this.managerThread.start();
		}
		
	}
	
	public void watch() throws KeeperException, InterruptedException,
	        IOException
	{
		// 查看workers的通知信息
		CenterConfig.me().getWorkersInfo().getOnlineWorkers()
		        .getWorkerRefreshPath().watch(this);
		// 监听所有在线的siteManager
		CenterConfig.me().getSiteManagersConfigInfo().getOnlineSiteManagers()
		        .watch(this);
	}
}
