package guang.crawler.hbaseConnector;

import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.core.WebURL;

import java.io.IOException;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestWebDataTable
{
	private static WebDataTableConnector	webDataTable;
	
	@AfterClass
	public static void close() throws IOException
	{
		TestWebDataTable.webDataTable.flush();
		TestWebDataTable.webDataTable.close();
	}
	
	@BeforeClass
	public static void setup() throws MasterNotRunningException,
	        ZooKeeperConnectionException, IOException
	{
		TestWebDataTable.webDataTable = new WebDataTableConnector(
		        "ubuntu-3,ubuntu-2,ubuntu-6,ubuntu-7,ubuntu-8", "2181");
		TestWebDataTable.webDataTable.open();
	}
	
	@Test
	public void test() throws IOException
	{
		WebURL webUrl = new WebURL();
		webUrl.setDocid(1);
		webUrl.setURL("http://agoodtest");
		webUrl.setDepth((short) 0);
		TestWebDataTable.webDataTable.addHtmlData(webUrl, "this is content",
		        false);
	}
	
}
