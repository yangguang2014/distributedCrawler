package guang.crawler.siteManager;

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
	
	/**
	 * 当前站点的种子URL列表
	 */
	private String	seedURL;
	
	/**
	 * 当前站点管理器的工作目录
	 */
	private String	baseDir;
	
	/**
	 * 站点管理器的监听端口
	 */
	private int	   listenPort;
	/**
	 * 当前站点的唯一标识
	 */
	private String	siteID;
	
	private SiteConfig()
	{
	}
	
	public String getBaseDir()
	{
		return this.baseDir;
	}
	
	public int getListenPort()
	{
		return this.listenPort;
	}
	
	public String getSeedURL()
	{
		return this.seedURL;
	}
	
	public String getSiteID()
	{
		return this.siteID;
	}
	
	public void setBaseDir(String baseDir)
	{
		this.baseDir = baseDir;
	}
	
	public void setListenPort(int listenPort)
	{
		this.listenPort = listenPort;
	}
	
	public void setSeedURL(String seedURL)
	{
		this.seedURL = seedURL;
	}
	
	public void setSiteID(String siteID)
	{
		this.siteID = siteID;
	}
	
}
