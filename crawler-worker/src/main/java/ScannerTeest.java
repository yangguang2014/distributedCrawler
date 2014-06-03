import guang.crawler.connector.WebDataTableConnector;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;

public class ScannerTeest
{
	public static void main(String[] args) throws MasterNotRunningException,
	        ZooKeeperConnectionException, IOException
	{
		WebDataTableConnector connector = new WebDataTableConnector(
		        "ubuntu-3,ubuntu-6,ubuntu-8");
		connector.open();
		ResultScanner scanner = connector.scanTable(2);
		Iterator<Result> results = scanner.iterator();
		while (results.hasNext())
		{
			Result result = results.next();
			String[] data = connector.resultToHtmlData(result);
			System.out.println("url:" + data[0]);
		}
		connector.close();
		
	}
}
