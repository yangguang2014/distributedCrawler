package guang.crawler.sitemanager;

import guang.crawler.jsonServer.AcceptJsonServer;
import guang.crawler.jsonServer.JsonServer;
import guang.crawler.jsonServer.ServerStartException;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author yang
 */
public class TestJSONServer
{
	
	private static JsonServer	server;
	
	@BeforeClass
	public static void setUpClass() throws ServerStartException
	{
		String configFileName = TestJSONServer.class.getResource(
		        "/commandlet.xml").getPath();
		File configFile = new File(configFileName);
		String schemaFileName = TestJSONServer.class.getResource("/site.xsd")
		        .getPath();
		File schemaFile = new File(schemaFileName);
		TestJSONServer.server = new AcceptJsonServer(9001, 10, 2, configFile,
		        schemaFile);
		System.out.println("[OK] created.");
	}
	
	@AfterClass
	public static void tearDownClass()
	{
		TestJSONServer.server.shutdown();
		TestJSONServer.server.waitForStop();
		System.out.println("[OK] shutdown.");
	}
	
	@Test
	public void testServer() throws InterruptedException
	{
		TestJSONServer.server.start();
		System.out.println("[OK] started.");
		
	}
	
}
