package guang.crawler.controller;

import guang.crawler.connector.ZookeeperConnector;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Transaction;

public class UnHandledSiteInfo implements Zookeeperable
{
	private String	                 path;
	private final ZookeeperConnector	connector;
	private String	                 name;
	private String	                 seedSite;
	private static final String	     basePath	= CrawlerController.ROOT_PATH
	                                                  + CrawlerController.CONFIG_PATH
	                                                  + CrawlerController.UN_HANDLED_SITES;
	
	public UnHandledSiteInfo(String name, ZookeeperConnector connector)
	{
		this.name = name;
		this.path = UnHandledSiteInfo.basePath + "/" + name;
		this.connector = connector;
	}
	
	@Override
	public boolean delete(Transaction transaction) throws InterruptedException
	{
		return this.connector.simpleDelete(this.path, transaction);
		
	}
	
	public String getName()
	{
		
		return this.name;
	}
	
	public String getPath()
	{
		return this.path;
	}
	
	public String getSeedSite() throws InterruptedException
	{
		if (this.seedSite == null)
		{
			this.load();
		}
		return this.seedSite;
	}
	
	@Override
	public boolean load() throws InterruptedException
	{
		byte[] data = this.connector.getData(this.path);
		if (data != null)
		{
			this.seedSite = new String(data);
			return true;
		}
		return false;
		
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setSeedSite(String seedSite)
	{
		this.seedSite = seedSite;
	}
	
	@Override
	public boolean update(Transaction transaction) throws InterruptedException
	{
		boolean success = this.connector.simpleDelete(this.path, transaction);
		if (success)
		{
			this.path = UnHandledSiteInfo.basePath + "/" + this.name;
			String realPath = this.connector.createNode(this.path,
			        CreateMode.PERSISTENT, this.seedSite.getBytes());
			if (realPath != null)
			{
				return true;
			}
		}
		return false;
		
	}
}
