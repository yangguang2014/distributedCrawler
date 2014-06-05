package guang.crawler.connector;

import java.io.IOException;

import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;

public class TestWebDataTableConnector {
	public static void main(String[] args) throws MasterNotRunningException,
			ZooKeeperConnectionException, IOException {
		WebDataTableConnector connector = new WebDataTableConnector(
				"debian03,debian04,debian05");
		connector.open();
		try {
			Long[] ids = connector.getAvailableSiteIds();
			System.out.println(ids);
		} finally {
			connector.close();
		}
	}
}
