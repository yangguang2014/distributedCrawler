package guang.crawler.controller;

import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

public class CrawlerController
{
	public static final String	CONFIG_PATH	       = "/config";
	public static final String	HANDLED_SITES_PATH	= "/handledSites";
	public static final String	ROOT_PATH	       = "/crawler";
	public static final String	SITES_PATH	       = "/sites";
	public static final String	UN_HANDLED_SITES	= "/unHandledSites";
	public static final String	WORKERS_PATH	   = "/workers";
	
	public static void main(String[] args) throws IOException,
	        InterruptedException
	{
		CrawlerController controller = new CrawlerController(
		        "ubuntu-3,ubuntu-2,ubuntu-6,ubuntu-7,ubuntu-8");
		controller.clearConfig();
		controller.initConfig();
		controller.addSite("quanbenNovel", "http://www.quanben.com/");
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
			this.connector.createNode(CrawlerController.ROOT_PATH
			        + CrawlerController.CONFIG_PATH
			        + CrawlerController.UN_HANDLED_SITES + "/" + name,
			        CreateMode.PERSISTENT, seedSite.getBytes());
			return true;
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
	
	public List<SiteInfo> getUnHandledSites()
	        throws InterruptedException
	{
		String path = CrawlerController.ROOT_PATH
		        + CrawlerController.CONFIG_PATH
		        + CrawlerController.UN_HANDLED_SITES;
		List<String> childPaths = this.connector.getChildren(path);
		if (childPaths != null)
		{
			ArrayList<SiteInfo> result = new ArrayList<>(
			        childPaths.size());
			for (String cp : childPaths)
			{
				SiteInfo info = new SiteInfo(cp,
				        this.connector);
				result.add(info);
			}
			return result;
		}
		return null;
	}
	
	public boolean handleSite(SiteInfo site)
	{
		// 只有处于/crawler/config/unHandledSites下的站点才是可以被移动的。
		
		if (site.getPath().startsWith(
		        CrawlerController.ROOT_PATH + CrawlerController.CONFIG_PATH
		                + CrawlerController.UN_HANDLED_SITES))
		{
			String toPath = CrawlerController.ROOT_PATH
			        + CrawlerController.CONFIG_PATH
			        + CrawlerController.HANDLED_SITES_PATH + "/"
			        + site.getName();
			try
			{
				this.connector.moveTo(site.getPath(), toPath);
				return true;
			} catch (KeeperException | InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
		} else
		{ // 如果已经在处理了，那么就不需要考虑这个站点了。
			return true;
		}
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
			                + CrawlerController.UN_HANDLED_SITES,
			        CreateMode.PERSISTENT,
			        "sites that haven't been handled".getBytes());
		}
		if (path != null)
		{
			path = this.connector.checkAndCreateNode(
			        CrawlerController.ROOT_PATH + CrawlerController.CONFIG_PATH
			                + CrawlerController.HANDLED_SITES_PATH,
			        CreateMode.PERSISTENT,
			        "sites that have been handled".getBytes());
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
