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

/**
 * 连接HBase中存储爬取的数据的连接器
 *
 * @author sun
 *
 */
public class WebDataTableConnector {
	/**
	 * 每个采集点都有自己独立的表,该常量是表的名称的前缀.
	 */
	private static final String	             TABLE_PREFIX	     = "site-";
	/**
	 * HBase的配置信息
	 */
	private Configuration	                 hbaseConfig;
	/**
	 * 用来对HBase进行增删改查操作的管理器
	 */
	private HBaseAdmin	                     hbaseAdmin;
	/**
	 * HBase连接
	 */
	private HConnection	                     hConnection;
	/**
	 * 缓存的数据表,为了防止重复打开关闭.
	 */
	private HashMap<String, HTableInterface>	webDataTables;
	/**
	 * 存储主要数据的簇
	 */
	public final static String	             FAMILY_MAIN_DATA	 = "MAIN";
	/**
	 * 对主要数据进行支撑的簇
	 */
	public final static String	             FAMILY_SUPPORT_DATA	= "SUPPORT";
	/**
	 * 是否打开是对HBase的连接
	 */
	private boolean	                         opened	             = false;
	/**
	 * 缓冲区大小
	 */
	private final static long	             bufferSize	         = 1024;
	/**
	 * HBase需要的Zookeeper的连接字符串
	 */
	private String	                         zookeeperQuorum;
	
	/**
	 * 创建一个连接器
	 *
	 * @param zookeeperQuorum
	 *            HBase所需的Zookeeper连接器的地址.
	 */
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
	
	/**
	 * 关闭连接和所有打开的表.
	 *
	 * @throws IOException
	 */
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
	
	/**
	 * 创建一个HBase表.
	 *
	 * @param tableName
	 * @return
	 * @throws IOException
	 */
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
	
	/**
	 * 删除一个HBase表.
	 *
	 * @param tableName
	 * @return
	 * @throws IOException
	 */
	public boolean deleteTable(final String tableName) throws IOException {
		boolean disabled = this.hbaseAdmin.isTableDisabled(tableName);
		if (!disabled) {
			this.hbaseAdmin.disableTable(tableName);
		}
		this.hbaseAdmin.deleteTable(tableName);
		return true;
	}
	
	/**
	 * 刷新缓冲的数据.
	 *
	 * @throws IOException
	 */
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
	
	/**
	 * 该方法已经不用了,因为数据库表的结构发生了改变.
	 *
	 * @param tableName
	 * @param docid
	 * @return
	 * @throws IOException
	 */
	@Deprecated
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
	
	/**
	 * 加载已经存在的表,如果表不存在,会发生异常
	 * 
	 * @param tableName
	 * @return
	 * @throws IOException
	 */
	public HTableInterface loadTable(final String tableName) throws IOException {
		return this.hConnection.getTable(tableName);
	}
	
	/**
	 * 打开连接
	 * 
	 * @throws MasterNotRunningException
	 * @throws ZooKeeperConnectionException
	 * @throws IOException
	 */
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
