package guang.crawler.connector;

import guang.crawler.core.WebURL;

import java.io.IOException;
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
import org.apache.hadoop.hbase.util.Bytes;

public class WebDataTableConnector
{
	private Configuration	                 hbaseConfig;
	private HBaseAdmin	                     hbaseAdmin;
	private HConnection	                     hConnection;
	private HashMap<String, HTableInterface>	webDataTables;
	private final String	                 dataFamilyName	= "data";
	private boolean	                         opened	        = false;
	private final static long	             bufferSize	    = 1024;
	private String	                         zookeeperQuorum;
	private String	                         zookeeperClientPort;
	
	public WebDataTableConnector(String zookeeperQuorum,
	        String zookeeperClientPort)
	{
		this.zookeeperClientPort = zookeeperClientPort;
		this.zookeeperQuorum = zookeeperQuorum;
	}
	
	public void addHtmlData(WebURL webUrl, String html, boolean childFinished)
	        throws IOException
	{
		if (!this.opened)
		{
			throw new IOException("data base should be opened first.");
		}
		String tableName = webUrl.getSiteManagerName();
		HTableInterface webDataTable = this.webDataTables.get(tableName);
		if (webDataTable == null)
		{
			if (!this.tableExists(tableName))
			{
				webDataTable = this.createTable(tableName);
			} else
			{
				webDataTable = this.loadTable(tableName);
			}
			if (webDataTable != null)
			{
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
	}
	
	public void close() throws IOException
	{
		if (!this.opened)
		{
			return;
		}
		Iterator<Entry<String, HTableInterface>> tables = this.webDataTables
		        .entrySet().iterator();
		while (tables.hasNext())
		{
			tables.next().getValue().close();
		}
		if (this.hbaseAdmin != null)
		{
			this.hbaseAdmin.close();
		}
		if (this.hConnection != null)
		{
			this.hConnection.close();
		}
		this.opened = false;
	}
	
	public HTableInterface createTable(String tableName) throws IOException
	{
		
		HTableDescriptor tableDesc = new HTableDescriptor(
		        TableName.valueOf(tableName));
		HColumnDescriptor dataFamily = new HColumnDescriptor(
		        this.dataFamilyName);
		dataFamily.setMaxVersions(1);
		dataFamily.setBlockCacheEnabled(false);
		tableDesc.addFamily(dataFamily);
		this.hbaseAdmin.createTable(tableDesc);
		
		HTableInterface webDataTable = this.hConnection.getTable(tableName);
		webDataTable.setAutoFlush(false, true);
		webDataTable.setWriteBufferSize(WebDataTableConnector.bufferSize);
		return webDataTable;
	}
	
	public boolean deleteTable(String tableName) throws IOException
	{
		boolean disabled = this.hbaseAdmin.isTableDisabled(tableName);
		if (!disabled)
		{
			this.hbaseAdmin.disableTable(tableName);
		}
		this.hbaseAdmin.deleteTable(tableName);
		return true;
	}
	
	public void flush() throws IOException
	{
		if (!this.opened)
		{
			return;
		}
		Iterator<Entry<String, HTableInterface>> tables = this.webDataTables
		        .entrySet().iterator();
		while (tables.hasNext())
		{
			tables.next().getValue().flushCommits();
		}
	}
	
	public String[] getHtmlData(String tableName, int docid) throws IOException
	{
		if (!this.opened)
		{
			throw new IOException("data base should be opened first.");
		}
		HTableInterface webDataTable = this.webDataTables.get(tableName);
		if (webDataTable == null)
		{
			if (!this.tableExists(tableName))
			{
				webDataTable = this.createTable(tableName);
			} else
			{
				webDataTable = this.loadTable(tableName);
			}
			if (webDataTable != null)
			{
				this.webDataTables.put(tableName, webDataTable);
			}
		}
		Get get = new Get(Bytes.toBytes(docid));
		get.addFamily(Bytes.toBytes(this.dataFamilyName));
		Result result = webDataTable.get(get);
		if (result != null)
		{
			String[] data = new String[2];
			byte[] urlData = result.getValue(
			        Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("url"));
			if (urlData != null)
			{
				data[0] = Bytes.toString(urlData);
			}
			byte[] htmlData = result.getValue(
			        Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("html"));
			if (htmlData != null)
			{
				data[1] = Bytes.toString(htmlData);
			}
			return data;
		}
		return null;
	}
	
	public HTableInterface loadTable(String tableName) throws IOException
	{
		return this.hConnection.getTable(tableName);
	}
	
	public void open() throws MasterNotRunningException,
	        ZooKeeperConnectionException, IOException
	{
		if (this.opened)
		{
			return;
		}
		Configuration config = new Configuration();
		config.set("hbase.zookeeper.quorum", this.zookeeperQuorum);
		config.set("hbase.zookeeper.property.clientPort",
		        this.zookeeperClientPort);
		this.hbaseConfig = HBaseConfiguration.create(config);
		this.hbaseAdmin = new HBaseAdmin(this.hbaseConfig);
		this.hConnection = HConnectionManager
		        .createConnection(this.hbaseConfig);
		this.opened = true;
	}
	
	public boolean tableExists(String tableName) throws IOException
	{
		return this.hbaseAdmin.tableExists(tableName);
	}
	
}
