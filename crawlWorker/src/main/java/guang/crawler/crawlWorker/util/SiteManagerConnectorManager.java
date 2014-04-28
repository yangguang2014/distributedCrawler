package guang.crawler.crawlWorker.util;

import guang.crawler.connector.SiteManagerConnector;
import guang.crawler.controller.CrawlerController;
import guang.crawler.controller.SiteInfo;
import guang.crawler.core.DataPacket;
import guang.crawler.core.WebURL;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSON;

public class SiteManagerConnectorManager
{
	private HashMap<String, SiteManagerConnector>	      connectors;
	private CrawlerController	                          crawlerController;
	private Iterator<Entry<String, SiteManagerConnector>>	connectorIterator;
	
	public SiteManagerConnectorManager(CrawlerController controller)
	        throws UnknownHostException, IOException
	{
		this.crawlerController = controller;
		this.connectors = new HashMap<>();
	}
	
	public void exit() throws IOException
	{
		this.connectorIterator = this.connectors.entrySet().iterator();
		while (this.connectorIterator.hasNext())
		{
			this.exit(this.connectorIterator.next().getValue());
		}
		
	}
	
	public void exit(SiteManagerConnector connector) throws IOException
	{
		connector.send(DataPacket.EXIT_DATA_PACKET);
		
	}
	
	public LinkedList<WebURL> getURLs(int num) throws IOException,
	        InterruptedException
	{
		DataPacket data = new DataPacket("/url/get", null, null);
		HashMap<String, String> requestData = new HashMap<>();
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
		LinkedList<WebURL> urls = new LinkedList<>();
		for (int j = 0; j < num; j++)
		{
			if (!this.connectorIterator.hasNext())
			{
				this.connectorIterator = this.connectors.entrySet().iterator();
			}
			
			SiteManagerConnector connector = this.connectorIterator.next()
			        .getValue();
			connector.send(data);
			DataPacket result = connector.read();
			if (result != null)
			{
				int count = Integer.parseInt(result.getData().get("COUNT"));
				if (count > 0)
				{
					for (int i = 0; i < count; i++)
					{
						String url = result.getData().get("URL_LIST" + i);
						urls.add(JSON.parseObject(url, WebURL.class));
					}
				}
			}
			
		}
		return urls;
	}
	
	public void putData(WebURL parent, List<WebURL> outGoings)
	        throws IOException
	{
		DataPacket request = new DataPacket();
		request.setTitle("/url/put");
		HashMap<String, String> requestData = new HashMap<>();
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
		SiteManagerConnector connector = this.connectors.get(parent
		        .getSiteManagerName());
		connector.send(request);
		
	}
	
	public void refreshConnectors() throws InterruptedException
	{
		
		Set<String> keys = this.connectors.keySet();
		for (String key : keys)
		{
			try
			{
				SiteManagerConnector connector = this.connectors.get(key);
				this.exit(connector);
				connector.shutdown();
			} catch (IOException e)
			{
				// Skit it
				e.printStackTrace();
			}
		}
		this.connectors.clear();
		List<SiteInfo> sites = this.crawlerController.getHandledSites();
		if ((sites != null) && (sites.size() > 0))
		{
			for (SiteInfo site : sites)
			{
				String siteManagerAddress = site.getSiteManager();
				if (siteManagerAddress != null)
				{
					String[] addInfo = siteManagerAddress.split(":");
					SiteManagerConnector connector;
					try
					{
						connector = new SiteManagerConnector(addInfo[0],
						        Integer.parseInt(addInfo[1]));
						this.connectors.put(site.getName(), connector);
					} catch (NumberFormatException | IOException e)
					{
						// Skip it
						e.printStackTrace();
					}
					
				}
				
			}
		}
		
	}
	
}
