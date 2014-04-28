package guang.crawler.siteManager;

import guang.crawler.controller.CrawlerController;
import guang.crawler.controller.SiteInfo;

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
	
	private SiteInfo	      siteToHandle;
	private CrawlerController	crawlerController;
	
	/**
	 * 当前站点管理器的工作目录
	 */
	private String	          baseDir;
	
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
	}
	
	public String getBaseDir()
	{
		return this.baseDir;
	}
	
	public CrawlerController getCrawlerController()
	{
		return this.crawlerController;
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
	
	public void setBaseDir(String baseDir)
	{
		this.baseDir = baseDir;
	}
	
	public void setCrawlerController(CrawlerController crawlerController)
	{
		this.crawlerController = crawlerController;
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
