package guang.crawler.connector;

import guang.crawler.core.WebURL;

import java.io.IOException;

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
	private final String	  tableName	     = "webData_test";
	private Configuration	  hbaseConfig;
	private HBaseAdmin	      hbaseAdmin;
	private HConnection	      hConnection;
	private HTableInterface	  webDataTable;
	private final String	  dataFamilyName	= "data";
	private boolean	          opened	     = false;
	private final static long	bufferSize	 = 1024;
	private String	          zookeeperQuorum;
	private String	          zookeeperClientPort;
	
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
		Put put = new Put(Bytes.toBytes(webUrl.getDocid()));// 设置键值
		put.add(Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("depth"),
		        Bytes.toBytes(webUrl.getDepth()));
		put.add(Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("url"),
		        Bytes.toBytes(webUrl.getURL()));
		put.add(Bytes.toBytes(this.dataFamilyName), Bytes.toBytes("html"),
		        Bytes.toBytes(html));
		put.add(Bytes.toBytes(this.dataFamilyName),
		        Bytes.toBytes("childFinshed"), Bytes.toBytes(childFinished));
		this.webDataTable.put(put);
	}
	
	public void close() throws IOException
	{
		if (!this.opened)
		{
			return;
		}
		if (this.webDataTable != null)
		{
			this.webDataTable.close();
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
	
	public void flush() throws IOException
	{
		if (!this.opened)
		{
			return;
		}
		this.webDataTable.flushCommits();
	}
	
	public String[] getHtmlData(int docid) throws IOException
	{
		
		Get get = new Get(Bytes.toBytes(docid));
		get.addFamily(Bytes.toBytes(this.dataFamilyName));
		Result result = this.webDataTable.get(get);
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
		boolean exists = this.hbaseAdmin.tableExists(this.tableName);
		if (exists)
		{
			this.hbaseAdmin.disableTable(this.tableName);
			this.hbaseAdmin.deleteTable(this.tableName);
		}
		HTableDescriptor tableDesc = new HTableDescriptor(
		        TableName.valueOf(this.tableName));
		HColumnDescriptor dataFamily = new HColumnDescriptor(
		        this.dataFamilyName);
		dataFamily.setMaxVersions(1);
		dataFamily.setBlockCacheEnabled(false);
		tableDesc.addFamily(dataFamily);
		this.hbaseAdmin.createTable(tableDesc);
		
		this.webDataTable = this.hConnection.getTable(this.tableName);
		this.webDataTable.setAutoFlush(false, true);
		this.webDataTable.setWriteBufferSize(WebDataTableConnector.bufferSize);
		this.opened = true;
	}
	
}
