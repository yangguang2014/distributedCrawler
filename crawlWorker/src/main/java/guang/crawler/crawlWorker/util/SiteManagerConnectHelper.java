package guang.crawler.crawlWorker.util;

import guang.crawler.connector.SiteManagerConnector;
import guang.crawler.core.DataPacket;
import guang.crawler.core.PortDefine;
import guang.crawler.core.WebURL;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class SiteManagerConnectHelper
{
	private SiteManagerConnector	connector;
	
	public SiteManagerConnectHelper() throws UnknownHostException, IOException
	{
		this.connector = new SiteManagerConnector("localhost",
		        PortDefine.PORT_SITE_MANAGER);
	}
	
	public void exit() throws IOException
	{
		this.connector.send(DataPacket.EXIT_DATA_PACKET);
		
	}
	
	public LinkedList<WebURL> getURLs(int num) throws IOException
	{
		DataPacket data = new DataPacket("/url/get", null, null);
		HashMap<String, String> requestData = new HashMap<>();
		requestData.put("COUNT", "1");
		data.setData(requestData);
		this.connector.send(data);
		DataPacket result = this.connector.read();
		if (result != null)
		{
			int count = Integer.parseInt(result.getData().get("COUNT"));
			if (count > 0)
			{
				LinkedList<WebURL> urls = new LinkedList<>();
				
				for (int i = 0; i < count; i++)
				{
					String url = result.getData().get("URL_LIST" + i);
					urls.add(JSON.parseObject(url, WebURL.class));
				}
				return urls;
			}
		}
		return null;
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
		this.connector.send(request);
		
	}
	
}
