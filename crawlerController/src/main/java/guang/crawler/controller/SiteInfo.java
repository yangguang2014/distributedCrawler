package guang.crawler.controller;

import guang.crawler.connector.ZookeeperConnector;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Transaction;

public class SiteInfo extends ZookeeperElement
{
	private static final String	KEY_NAME	     = "name";
	private static final String	KEY_SEED	     = "seedSite";
	private static final String	KEY_HANDLED	     = "handled";
	private static final String	KEY_SITE_MANAGER	= "siteManager";
	
	public SiteInfo(String path, ZookeeperConnector connector)
	{
		super(path, connector);
	}
	
	public String getName() throws InterruptedException
	{
		return this.get(SiteInfo.KEY_NAME);
	}
	
	public String getSeedSite() throws InterruptedException
	{
		return this.get(SiteInfo.KEY_SEED);
	}
	
	public String getSiteManager() throws InterruptedException
	{
		return this.get(SiteInfo.KEY_SITE_MANAGER);
	}
	
	public boolean isHandled() throws InterruptedException
	{
		return Boolean.parseBoolean(this.get(SiteInfo.KEY_HANDLED));
	}
	
	public void setHandled(boolean isHandled) throws InterruptedException
	{
		this.put(SiteInfo.KEY_HANDLED, Boolean.toString(isHandled), true);
	}
	
	public void setName(String name) throws InterruptedException
	{
		this.put(SiteInfo.KEY_NAME, name, true);
	}
	
	public void setSeedSite(String seedSite) throws InterruptedException
	{
		this.put(SiteInfo.KEY_SEED, seedSite, true);
	}
	
	public void setSiteManager(String addr) throws InterruptedException
	{
		this.put(SiteInfo.KEY_SITE_MANAGER, addr, true);
	}
	
	@Override
	public boolean update(String key, Transaction transaction)
	        throws InterruptedException
	{
		if (SiteInfo.KEY_SITE_MANAGER.equals(key))
		{
			this.connector.createNode(this.path + "/"
			        + SiteInfo.KEY_SITE_MANAGER, CreateMode.EPHEMERAL, this
			        .get(key).getBytes());
		}
		return super.update(key, transaction);
	}
}
