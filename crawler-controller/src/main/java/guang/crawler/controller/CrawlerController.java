package guang.crawler.controller;

import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;

public class CrawlerController
{
	public static final String	CONFIG_PATH	 = "/config";
	public static final String	ROOT_PATH	 = "/crawler";
	public static final String	SITES_PATH	 = "/sites";
	public static final String	WORKERS_PATH	= "/workers";
	
	public static void main(String[] args) throws IOException,
	        InterruptedException
	{
		CrawlerController controller = new CrawlerController(
		        "ubuntu-3,ubuntu-2,ubuntu-6,ubuntu-7,ubuntu-8");
		controller.clearConfig();
		controller.initConfig();
		controller.addSite("quanbenNovel", "http://www.quanben.com/");
		controller.addSite("tudou", "http://www.tudou.com/");
		controller.shutdown();
	}
	
	private ZookeeperConnector	connector;
	
	public CrawlerController(String connectString) throws IOException
	{
		this.connector = new ZookeeperConnector(connectString);
	}
	
	public boolean addSite(String name, String seedSite)
	{
		try
		{
			String path = CrawlerController.ROOT_PATH
			        + CrawlerController.CONFIG_PATH
			        + CrawlerController.SITES_PATH + "/" + name;
			this.connector.createNode(path, CreateMode.PERSISTENT,
			        "".getBytes());
			SiteInfo siteInfo = new SiteInfo(path, name, this.connector);
			siteInfo.setSeedSite(seedSite);
			return siteInfo.update(null);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean clearConfig()
	{
		try
		{
			boolean success = this.connector.recursiveDelete(
			        CrawlerController.ROOT_PATH, null);
			return success;
		} catch (InterruptedException e)
		{
			return false;
		}
	}
	
	public List<SiteInfo> getHandledSites() throws InterruptedException
	{
		String path = CrawlerController.ROOT_PATH
		        + CrawlerController.CONFIG_PATH + CrawlerController.SITES_PATH;
		List<String> childPaths = this.connector.getChildren(path);
		if (childPaths != null)
		{
			ArrayList<SiteInfo> result = new ArrayList<>(childPaths.size());
			for (String cp : childPaths)
			{
				SiteInfo info = new SiteInfo(path + "/" + cp, cp,
				        this.connector);
				if (info.isHandled())
				{
					result.add(info);
				}
				
			}
			return result;
		}
		return null;
	}
	
	public List<SiteInfo> getUnHandledSites() throws InterruptedException
	{
		String path = CrawlerController.ROOT_PATH
		        + CrawlerController.CONFIG_PATH + CrawlerController.SITES_PATH;
		List<String> childPaths = this.connector.getChildren(path);
		if (childPaths != null)
		{
			ArrayList<SiteInfo> result = new ArrayList<>(childPaths.size());
			for (String cp : childPaths)
			{
				SiteInfo info = new SiteInfo(path + "/" + cp, cp,
				        this.connector);
				if (!info.isHandled())
				{
					result.add(info);
				}
				
			}
			return result;
		}
		return null;
	}
	
	/**
	 * 将当前站点注册给某个站点管理器管理。
	 * 
	 * @param site
	 * @return
	 * @throws InterruptedException
	 */
	public boolean handleSite(SiteInfo site) throws InterruptedException
	{
		if (site.getPath().startsWith(
		        CrawlerController.ROOT_PATH + CrawlerController.CONFIG_PATH
		                + CrawlerController.SITES_PATH))
		{
			boolean locked = site.lock();
			if (locked)
			{
				try
				{
					boolean handled = site.isHandled();
					if (!handled)
					{
						site.setHandled(true);
						return true;
					}
				} finally
				{
					site.unlock();
				}
			}
		}
		return false;
	}
	
	public boolean initConfig() throws InterruptedException
	{
		String path = this.connector.checkAndCreateNode(
		        CrawlerController.ROOT_PATH, CreateMode.PERSISTENT,
		        "crawler config node".getBytes());
		if (path != null)
		{
			path = this.connector
			        .checkAndCreateNode(CrawlerController.ROOT_PATH
			                + CrawlerController.CONFIG_PATH,
			                CreateMode.PERSISTENT,
			                "config the crawler".getBytes());
		}
		if (path != null)
		{
			path = this.connector.checkAndCreateNode(
			        CrawlerController.ROOT_PATH + CrawlerController.CONFIG_PATH
			                + CrawlerController.SITES_PATH,
			        CreateMode.PERSISTENT,
			        "sites that haven't been handled".getBytes());
		}
		if (path != null)
		{
			path = this.connector.checkAndCreateNode(
			        CrawlerController.ROOT_PATH + CrawlerController.SITES_PATH,
			        CreateMode.PERSISTENT,
			        "seed sites that need to be crawlled".getBytes());
		}
		if (path != null)
		{
			path = this.connector
			        .checkAndCreateNode(CrawlerController.ROOT_PATH
			                + CrawlerController.WORKERS_PATH,
			                CreateMode.PERSISTENT,
			                "workers in this system.".getBytes());
		}
		return path != null;
	}
	
	public void shutdown() throws InterruptedException
	{
		this.connector.shutdown();
	}
}
