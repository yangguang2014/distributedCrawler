package guang.crawler.connector;

import guang.crawler.commons.DataField;
import guang.crawler.commons.DataFields;
import guang.crawler.commons.WebURL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
	private static final String	             TABLE_PREFIX	     = "site-";
	private Configuration	                 hbaseConfig;
	private HBaseAdmin	                     hbaseAdmin;
	private HConnection	                     hConnection;
	private HashMap<String, HTableInterface>	webDataTables;
	/**
	 * 存储主要数据的簇
	 */
	public final static String	             FAMILY_MAIN_DATA	 = "MAIN";
	/**
	 * 对主要数据进行支撑的簇
	 */
	public final static String	             FAMILY_SUPPORT_DATA	= "SUPPORT";
	private boolean	                         opened	             = false;
	private final static long	             bufferSize	         = 1024;
	private String	                         zookeeperQuorum;

	public WebDataTableConnector(final String zookeeperQuorum) {
		this.zookeeperQuorum = zookeeperQuorum;
		this.webDataTables = new HashMap<String, HTableInterface>();
	}
	
	/**
	 * 向HBase中插入一系列的域
	 *
	 * @param webUrl
	 * @param dataFields
	 * @throws IOException
	 */
	public void addDataFields(final WebURL webUrl, final DataFields dataFields)
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
		HashMap<String, LinkedList<DataField>> fields = dataFields.getAllFileds();
		Set<String> keys = fields.keySet();
		for (String key : keys) {
			LinkedList<DataField> data = fields.get(key);
			if ((data == null) || (data.size() == 0)) {
				continue;
			}
			Put put = new Put(Bytes.toBytes(key));// 设置键值
			for (DataField field : data) {
				put.add(Bytes.toBytes(field.getDataFamily()),
				        Bytes.toBytes(field.getColumnName()),
				        Bytes.toBytes(field.getData()));
			}
			webDataTable.put(put);
		}
		webDataTable.flushCommits();
		
	}

	public void addHtmlData(final WebURL webUrl, final String html,
	        final boolean childFinished) throws IOException {
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
		put.add(Bytes.toBytes(WebDataTableConnector.FAMILY_MAIN_DATA),
		        Bytes.toBytes("depth"), Bytes.toBytes(webUrl.getDepth()));
		put.add(Bytes.toBytes(WebDataTableConnector.FAMILY_MAIN_DATA),
		        Bytes.toBytes("url"), Bytes.toBytes(webUrl.getURL()));
		put.add(Bytes.toBytes(WebDataTableConnector.FAMILY_MAIN_DATA),
		        Bytes.toBytes("html"), Bytes.toBytes(html));
		put.add(Bytes.toBytes(WebDataTableConnector.FAMILY_MAIN_DATA),
		        Bytes.toBytes("childFinshed"), Bytes.toBytes(childFinished));
		webDataTable.put(put);

		webDataTable.flushCommits();
	}

	public void close() throws IOException {
		if (!this.opened) {
			return;
		}
		Iterator<Entry<String, HTableInterface>> tables = this.webDataTables.entrySet()
		                                                                    .iterator();
		while (tables.hasNext()) {
			tables.next()
			      .getValue()
			      .close();
		}
		if (this.hbaseAdmin != null) {
			this.hbaseAdmin.close();
		}
		if (this.hConnection != null) {
			this.hConnection.close();
		}
		this.opened = false;
	}

	public HTableInterface createTable(final String tableName)
	        throws IOException {

		HTableDescriptor tableDesc = new HTableDescriptor(
		        TableName.valueOf(tableName));
		HColumnDescriptor dataFamily = new HColumnDescriptor(
		        WebDataTableConnector.FAMILY_MAIN_DATA);
		dataFamily.setMaxVersions(1);
		dataFamily.setBlockCacheEnabled(false);
		tableDesc.addFamily(dataFamily);

		HColumnDescriptor supportDataFamily = new HColumnDescriptor(
		        WebDataTableConnector.FAMILY_SUPPORT_DATA);
		supportDataFamily.setMaxVersions(1);
		supportDataFamily.setBlockCacheEnabled(false);
		tableDesc.addFamily(supportDataFamily);
		this.hbaseAdmin.createTable(tableDesc);

		HTableInterface webDataTable = this.hConnection.getTable(tableName);
		webDataTable.setAutoFlush(true, true);
		webDataTable.setWriteBufferSize(WebDataTableConnector.bufferSize);
		return webDataTable;
	}

	public boolean deleteTable(final String tableName) throws IOException {
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
		Iterator<Entry<String, HTableInterface>> tables = this.webDataTables.entrySet()
		                                                                    .iterator();
		while (tables.hasNext()) {
			tables.next()
			      .getValue()
			      .flushCommits();
		}
	}

	/**
	 * 获取具有某种类型名称的表
	 *
	 * @return
	 * @throws IOException
	 */
	public List<String> getAllTables(final String pattern) throws IOException {
		HTableDescriptor[] tableDescriptors = this.hbaseAdmin.listTables(pattern);
		if ((tableDescriptors == null) || (tableDescriptors.length == 0)) {
			return null;
		}
		ArrayList<String> result = new ArrayList<String>();
		for (HTableDescriptor table : tableDescriptors) {
			String tableName = new String(table.getName());
			result.add(tableName);
		}
		return result;
	}

	/**
	 * 获取当前已经爬取了数据的站点的ID
	 *
	 * @return
	 * @throws IOException
	 */
	public Long[] getAvailableSiteIds() throws IOException {
		HTableDescriptor[] tableDescriptors = this.hbaseAdmin.listTables("site-\\d*");
		if ((tableDescriptors == null) || (tableDescriptors.length == 0)) {
			return null;
		}
		ArrayList<Long> result = new ArrayList<Long>();
		for (HTableDescriptor table : tableDescriptors) {
			String tableName = new String(table.getName());
			try {
				long siteId = Long.parseLong(tableName.substring(WebDataTableConnector.TABLE_PREFIX.length()));
				result.add(siteId);
			} catch (NumberFormatException e) {
				continue;
			}
		}
		Long[] resultArray = new Long[result.size()];
		return result.toArray(resultArray);
	}

	public String[] getHtmlData(final String tableName, final int docid)
	        throws IOException {
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
		get.addFamily(Bytes.toBytes(WebDataTableConnector.FAMILY_MAIN_DATA));
		Result result = webDataTable.get(get);
		return this.resultToHtmlData(result);
	}

	public HTableInterface loadTable(final String tableName) throws IOException {
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
		this.hConnection = HConnectionManager.createConnection(this.hbaseConfig);
		this.opened = true;
	}

	public String[] resultToHtmlData(final Result result) {
		if (result != null) {
			String[] data = new String[2];
			byte[] urlData = result.getValue(Bytes.toBytes(WebDataTableConnector.FAMILY_MAIN_DATA),
			                                 Bytes.toBytes("url"));
			if (urlData != null) {
				data[0] = Bytes.toString(urlData);
			}
			byte[] htmlData = result.getValue(Bytes.toBytes(WebDataTableConnector.FAMILY_MAIN_DATA),
			                                  Bytes.toBytes("html"));
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
	public ResultScanner scanTable(final long siteId) throws IOException {
		HTableInterface iface = this.loadTable(WebDataTableConnector.TABLE_PREFIX
		        + siteId);
		if (iface != null) {
			ResultScanner scanner = iface.getScanner("data".getBytes());
			return scanner;
		}
		return null;
	}

	public boolean tableExists(final String tableName) throws IOException {
		return this.hbaseAdmin.tableExists(tableName);
	}

}
