package guang.crawler.crawlWorker;

import guang.crawler.connector.SiteManagerConnector;
import guang.crawler.core.DataPacket;
import guang.crawler.core.WebURL;
import guang.crawler.crawlWorker.fetcher.WebGeter;
import guang.crawler.jsonServer.AcceptJsonServer;
import guang.crawler.jsonServer.JsonServer;
import guang.crawler.jsonServer.ServerStartException;
import guang.crawler.sitemanager.TestJSONServer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author yang
 */
public class TestSiteManagerConnector
{
	
	private static SiteManagerConnector	connector;
	private static JsonServer	        server;
	
	@BeforeClass
	public static void setUpClass() throws IOException, ServerStartException
	{
		String configFileName = TestJSONServer.class.getResource(
		        "/commandlet.xml").getPath();
		File configFile = new File(configFileName);
		String schemaFileName = TestJSONServer.class.getResource("/site.xsd")
		        .getPath();
		File schemaFile = new File(schemaFileName);
		TestSiteManagerConnector.server = new AcceptJsonServer(9001, 10, 2,
		        configFile, schemaFile);
		TestSiteManagerConnector.server.start();
		TestSiteManagerConnector.connector = new SiteManagerConnector(
		        "localhost", 9001);
	}
	
	@AfterClass
	public static void tearDownClass() throws IOException
	{
		TestSiteManagerConnector.connector.shutdown();
		TestSiteManagerConnector.server.shutdown();
		TestSiteManagerConnector.server.waitForStop();
		System.out.println("[OK] shutdown.");
	}
	
	@Test
	public void sendData()
	{
		DataPacket data = new DataPacket("/url/get", null, null);
		HashMap<String, String> requestData = new HashMap<>();
		requestData.put("COUNT", "1");
		data.setData(requestData);
		try
		{
			TestSiteManagerConnector.connector.send(data);
			DataPacket result = TestSiteManagerConnector.connector.read();
			if (result != null)
			{
				int count = Integer.parseInt(result.getData().get("COUNT"));
				WebGeter wget = new WebGeter();
				for (int i = 0; i < count; i++)
				{
					String url = result.getData().get("URL_LIST" + i);
					WebURL wurl = new WebURL();
					wurl.setURL(url);
					wget.processUrl(wurl);
					
				}
				System.out.println(result.getData());
			}
			TestSiteManagerConnector.connector
			        .send(DataPacket.EXIT_DATA_PACKET);
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
