package guang.crawler.siteManager;

import guang.crawler.controller.CrawlerController;
import guang.crawler.controller.SiteInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SiteConfig
{
	private static SiteConfig	config;
	
	public static SiteConfig getConfig()
	{
		if (SiteConfig.config == null)
		{
			SiteConfig.config = new SiteConfig();
		}
		return SiteConfig.config;
	}
	
	private Properties	      configProperties;
	
	private String	          crawlerHome;
	private SiteInfo	      siteToHandle;
	private CrawlerController	crawlerController;
	/**
	 * 当前站点管理器的工作目录
	 */
	private String	          workDir;
	/**
	 * 站点管理器的监听端口
	 */
	private int	              listenPort;
	
	/**
	 * 当前站点的唯一标识
	 */
	private String	          siteID;
	
	private SiteConfig()
	{
		this.configProperties = new Properties();
		this.crawlerHome = System.getProperty("crawler.home");
		File file = new File(this.crawlerHome
		        + "/conf/site-manager/site-manager.config");
		if (file.exists())
		{
			try
			{
				this.configProperties.load(new FileInputStream(file));
			} catch (IOException e)
			{
				System.err.println("Error load site-manager config file");
			}
		}
		this.workDir = this.configProperties
		        .getProperty("crawler.site-manager.workdir");
	}
	
	public CrawlerController getCrawlerController()
	{
		return this.crawlerController;
	}
	
	public String getCrawlerHome()
	{
		return this.crawlerHome;
	}
	
	public int getListenPort()
	{
		return this.listenPort;
	}
	
	public String getSiteID()
	{
		return this.siteID;
	}
	
	public SiteInfo getSiteToHandle()
	{
		return this.siteToHandle;
	}
	
	public String getWorkDir()
	{
		return this.workDir;
	}
	
	public void setCrawlerController(CrawlerController crawlerController)
	{
		this.crawlerController = crawlerController;
	}
	
	public void setCrawlerHome(String crawlerHome)
	{
		this.crawlerHome = crawlerHome;
	}
	
	public void setListenPort(int listenPort)
	{
		this.listenPort = listenPort;
	}
	
	public void setSiteID(String siteID)
	{
		this.siteID = siteID;
	}
	
	public void setSiteToHandle(SiteInfo siteToHandled)
	{
		this.siteToHandle = siteToHandled;
	}
	
}
