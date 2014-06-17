package guang.crawler.connector;

import guang.crawler.commons.WebURL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

public class WebDataTableConnector {
	private static final String TABLE_PREFIX = "site-";
	private Configuration hbaseConfig;
	private HBaseAdmin hbaseAdmin;
	private HConnection hConnection;
	private HashMap<String, HTableInterface> webDataTables;
	private final String dataFamilyName = "data";
	private boolean opened = false;
	private final static long bufferSize = 1024;
	private String zookeeperQuorum;

	public WebDataTableConnector(String zookeeperQuorum) {
		this.zookeeperQuorum = zookeeperQuorum;
		this.webDataTables = new HashMap<String, HTableInterface>();
	}

	public void addHtmlData(WebURL webUrl, String html, boolean childFinished)
			throws IOException {
		if (!this.opened) {
			throw new IOException("data base should be opened first.");
		}
		String tableName = WebDataTableConnector.TABLE_PREFIX
				+ webUrl.getSiteId();
		HTableInterface webDataTable = this.webDataTables.get(tableName);
		if (webDataTable == null) {
			if (!this.tableExists(tableName)) {
				webDataTable = this.createTable(tableName);
			} else {
				webDataTable = this.loadTable(tableName);
			}
			if (webDataTable != null) {
				this.webDataTables.put(tableName, webDataTable);
			}
		}
		Put put = new Put(Bytes.toBytes(webUrl.getDocid()));// 设置键值
		put.add(Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("depth"),
				Bytes.toBytes(webUrl.getDepth()));
		put.add(Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("url"),
				Bytes.toBytes(webUrl.getURL()));
		put.add(Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("html"),
				Bytes.toBytes(html));
		put.add(Bytes.toBytes(this.dataFamilyName),
				Bytes.toBytes("childFinshed"), Bytes.toBytes(childFinished));
		webDataTable.put(put);
		webDataTable.flushCommits();
	}

	public void close() throws IOException {
		if (!this.opened) {
			return;
		}
		Iterator<Entry<String, HTableInterface>> tables = this.webDataTables
				.entrySet().iterator();
		while (tables.hasNext()) {
			tables.next().getValue().close();
		}
		if (this.hbaseAdmin != null) {
			this.hbaseAdmin.close();
		}
		if (this.hConnection != null) {
			this.hConnection.close();
		}
		this.opened = false;
	}

	public HTableInterface createTable(String tableName) throws IOException {

		HTableDescriptor tableDesc = new HTableDescriptor(
				TableName.valueOf(tableName));
		HColumnDescriptor dataFamily = new HColumnDescriptor(
				this.dataFamilyName);
		dataFamily.setMaxVersions(1);
		dataFamily.setBlockCacheEnabled(false);
		tableDesc.addFamily(dataFamily);
		this.hbaseAdmin.createTable(tableDesc);

		HTableInterface webDataTable = this.hConnection.getTable(tableName);
		webDataTable.setAutoFlush(true, true);
		webDataTable.setWriteBufferSize(WebDataTableConnector.bufferSize);
		return webDataTable;
	}

	public boolean deleteTable(String tableName) throws IOException {
		boolean disabled = this.hbaseAdmin.isTableDisabled(tableName);
		if (!disabled) {
			this.hbaseAdmin.disableTable(tableName);
		}
		this.hbaseAdmin.deleteTable(tableName);
		return true;
	}

	public void flush() throws IOException {
		if (!this.opened) {
			return;
		}
		Iterator<Entry<String, HTableInterface>> tables = this.webDataTables
				.entrySet().iterator();
		while (tables.hasNext()) {
			tables.next().getValue().flushCommits();
		}
	}

	/**
	 * 获取当前已经爬取了数据的站点的ID
	 * 
	 * @return
	 * @throws IOException
	 */
	public Long[] getAvailableSiteIds() throws IOException {
		HTableDescriptor[] tableDescriptors = this.hbaseAdmin
				.listTables("site-\\d*");
		if ((tableDescriptors == null) || (tableDescriptors.length == 0)) {
			return null;
		}
		ArrayList<Long> result = new ArrayList<Long>();
		for (HTableDescriptor table : tableDescriptors) {
			String tableName = new String(table.getName());
			try {
				long siteId = Long
						.parseLong(tableName
								.substring(WebDataTableConnector.TABLE_PREFIX
										.length()));
				result.add(siteId);
			} catch (NumberFormatException e) {
				continue;
			}
		}
		Long[]resultArray=new Long[result.size()];
		return result.toArray(resultArray);
	}

	public String[] getHtmlData(String tableName, int docid) throws IOException {
		if (!this.opened) {
			throw new IOException("data base should be opened first.");
		}
		HTableInterface webDataTable = this.webDataTables.get(tableName);
		if (webDataTable == null) {
			if (!this.tableExists(tableName)) {
				webDataTable = this.createTable(tableName);
			} else {
				webDataTable = this.loadTable(tableName);
			}
			if (webDataTable != null) {
				this.webDataTables.put(tableName, webDataTable);
			}
		}
		Get get = new Get(Bytes.toBytes(docid));
		get.addFamily(Bytes.toBytes(this.dataFamilyName));
		Result result = webDataTable.get(get);
		return this.resultToHtmlData(result);
	}

	public HTableInterface loadTable(String tableName) throws IOException {
		return this.hConnection.getTable(tableName);
	}

	public void open() throws MasterNotRunningException,
			ZooKeeperConnectionException, IOException {
		if (this.opened) {
			return;
		}
		Configuration config = new Configuration();
		config.set("hbase.zookeeper.quorum", this.zookeeperQuorum);
		this.hbaseConfig = HBaseConfiguration.create(config);
		this.hbaseAdmin = new HBaseAdmin(this.hbaseConfig);
		this.hConnection = HConnectionManager
				.createConnection(this.hbaseConfig);
		this.opened = true;
	}

	public String[] resultToHtmlData(Result result) {
		if (result != null) {
			String[] data = new String[2];
			byte[] urlData = result.getValue(
					Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("url"));
			if (urlData != null) {
				data[0] = Bytes.toString(urlData);
			}
			byte[] htmlData = result.getValue(
					Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("html"));
			if (htmlData != null) {
				data[1] = Bytes.toString(htmlData);
			}
			return data;
		}
		return null;
	}

	/**
	 * read data according to the site id
	 * 
	 * @param siteId
	 * @return
	 * @throws IOException
	 */
	public ResultScanner scanTable(long siteId) throws IOException {
		HTableInterface iface = this
				.loadTable(WebDataTableConnector.TABLE_PREFIX + siteId);
		if (iface != null) {
			ResultScanner scanner = iface.getScanner("data".getBytes());
			return scanner;
		}
		return null;
	}

	public boolean tableExists(String tableName) throws IOException {
		return this.hbaseAdmin.tableExists(tableName);
	}

}
