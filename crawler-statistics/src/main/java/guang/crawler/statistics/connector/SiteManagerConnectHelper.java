package guang.crawler.statistics.connector;

import guang.crawler.connector.SiteManagerConnector;
import guang.crawler.core.DataPacket;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.alibaba.fastjson.JSONException;

public class SiteManagerConnectHelper
{
	private SiteManagerConnector	connector;
	
	public SiteManagerConnectHelper(String host, int port)
	        throws UnknownHostException, IOException
	{
		this.connector = new SiteManagerConnector(host, port);
	}
	
	public void exit() throws IOException
	{
		this.connector.send(DataPacket.EXIT_DATA_PACKET);
		this.connector.shutdown();
		
	}
	
	public long[] getStatics() throws IOException
	{
		DataPacket data = new DataPacket("/statistics/get", null, null);
		this.connector.send(data);
		try
		{
			data = this.connector.read();
		} catch (JSONException e)
		{
			data = null;
		}
		if (data != null)
		{
			HashMap<String, String> statistics = data.getData();
			long todoSize = Long.parseLong(statistics.get("TODO"));
			long working = Long.parseLong(statistics.get("WORKING"));
			long finished = Long.parseLong(statistics.get("FINISHED"));
			return new long[] { todoSize, working, finished };
		}
		return null;
	}
	
}
