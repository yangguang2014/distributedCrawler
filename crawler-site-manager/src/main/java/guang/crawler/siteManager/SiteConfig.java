package guang.crawler.siteManager;

import guang.crawler.controller.CrawlerController;
import guang.crawler.controller.SiteInfo;
import guang.crawler.util.PropertiesHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SiteConfig
{
	private static SiteConfig	config;

	public static SiteConfig me()
	{
		if (SiteConfig.config == null)
		{
			SiteConfig.config = new SiteConfig();
		}
		return SiteConfig.config;
	}

	/**
	 * 从配置文件加载的属性信息
	 */
	private Properties	      configProperties;
	/**
	 * crawler的home
	 */
	private String	          crawlerHome;
	/**
	 * 将要工作的种子站点
	 */
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
	/**
	 * 作业超时的时间
	 */
	private long	          jobTimeout	     = 5 * 60 * 1000;

	/**
	 * 作业重试的次数
	 */
	private int	              jobTryTime	     = 3;

	/**
	 * 工作队列的清理时间间隔
	 */
	private long	          queueCleanerPeriod	= 100000;

	private boolean	          backTime	         = false;
	/**
	 * 运行hadoop时的用户
	 */
	private String	          hadoopUser;
	/**
	 * hadoop master节点的URL
	 */
	private String	          hadoopURL;

	/**
	 * 备份的版本号
	 */
	private int	              backupVersion;

	private String	          hadoopPath	     = "/home/crawler";

	private SiteConfig()
	{
	}

	public int getBackupVersion()
	{
		return this.backupVersion;
	}

	public CrawlerController getCrawlerController()
	{
		return this.crawlerController;
	}

	public String getCrawlerHome()
	{
		return this.crawlerHome;
	}

	public String getHadoopPath()
	{
		return this.hadoopPath;
	}

	public String getHadoopURL()
	{
		return this.hadoopURL;
	}

	public String getHadoopUser()
	{
		return this.hadoopUser;
	}

	public long getJobTimeout()
	{
		return this.jobTimeout;
	}

	public int getJobTryTime()
	{
		return this.jobTryTime;
	}

	public int getListenPort()
	{
		return this.listenPort;
	}

	public long getQueueCleanerPeriod()
	{
		return this.queueCleanerPeriod;
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

	public SiteConfig init() throws SiteManagerException
	{

		this.crawlerHome = System.getProperty("crawler.home");
		this.workDir = this.crawlerHome + "/work";
		File workdirFile = new File(this.workDir);
		if (!workdirFile.exists())
		{
			workdirFile.mkdirs();
		}
		this.initProperties();
		return this;
	}

	private void initProperties() throws SiteManagerException
	{
		this.configProperties = new Properties();
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
		this.jobTimeout = PropertiesHelper.readLong(this.configProperties,
				"crawler.site-manager.job.timeout", this.jobTimeout);
		this.jobTryTime = PropertiesHelper.readInt(this.configProperties,
				"crawler.site-manager.job.tryTime", this.jobTryTime);
		this.queueCleanerPeriod = PropertiesHelper.readLong(
				this.configProperties,
				"crawler.site-manager.queue.cleaner.period",
				this.queueCleanerPeriod);
		this.hadoopUser = PropertiesHelper.readString(this.configProperties,
				"crawler.site-manager.hadoop.user",
				System.getProperty("user.name"));
		System.setProperty("HADOOP_USER_NAME", this.hadoopUser);
		this.hadoopURL = PropertiesHelper.readString(this.configProperties,
				"crawler.site-manager.hadoop.url", null);
		if (this.hadoopURL == null)
		{
			throw new SiteManagerException(
					"config site error!",
					new IllegalArgumentException(
							"crawler.site-manager.backuper.hadoop.url property should not be null!"));
		}
		this.hadoopPath = PropertiesHelper.readString(this.configProperties,
				"crawler.site-manager.hadoop.path", this.hadoopPath);
	}

	public boolean isBackupTime()
	{
		return this.backTime;
	}

	public void setBackTime(boolean backTime)
	{
		this.backTime = backTime;
	}
	
	public void setBackupVersion(int backupVersion)
	{
		this.backupVersion = backupVersion;
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
