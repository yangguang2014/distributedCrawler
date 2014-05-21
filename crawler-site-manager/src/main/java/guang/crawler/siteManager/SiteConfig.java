package guang.crawler.siteManager;

import guang.crawler.controller.CrawlerController;
import guang.crawler.controller.SiteInfo;
import guang.crawler.siteManager.util.IOHelper;
import guang.crawler.util.PropertiesHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
	 * zookeeper的连接字符串
	 */
	private String	          zookeeperQuorum;
	
	/**
	 * 备份的版本号
	 */
	private int	              backupVersion;

	private String	          hadoopPath	     = "/home/crawler";

	/**
	 * 备份的时间，默认设置为1个小时
	 */
	private long	          backupPeriod	     = 3600000;

	private SiteConfig()
	{
	}

	public long getBackupPeriod()
	{
		return this.backupPeriod;
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

	public String getZookeeperQuorum()
	{
		return this.zookeeperQuorum;
	}
	
	public SiteConfig init() throws SiteManagerException
	{
		this.crawlerHome = System.getProperty("crawler.home");
		this.initWorkDir();
		this.initProperties();
		try
		{
			this.initHandleSite();
		} catch (IOException | InterruptedException e)
		{
			throw new SiteManagerException("No site to handle", e);
		}
		return this;
	}

	private void initHandleSite() throws IOException, InterruptedException
	{
		CrawlerController controller = new CrawlerController(
				this.getZookeeperQuorum());
		List<SiteInfo> unHandledSites = controller.getUnHandledSites();
		SiteInfo siteToHandle = null;
		if ((unHandledSites == null) || (unHandledSites.size() == 0))
		{
			System.out.println("No site to crawl.");
			return;
		} else
		{

			for (SiteInfo info : unHandledSites)
			{
				boolean success = controller.handleSite(info);
				if (success)
				{
					siteToHandle = info;
					break;
				}
			}
		}
		if (siteToHandle == null)
		{
			System.out.println("No site to crawl.");
			return;
		}
		this.setSiteID(siteToHandle.getName());
		this.setSiteToHandle(siteToHandle);
		this.setCrawlerController(controller);
		System.out.println("handle site " + siteToHandle.getSeedSite());
		
	}
	
	private void initProperties() throws SiteManagerException
	{
		this.configProperties = new Properties();
		PropertiesHelper.loadConfigFile(new File(this.crawlerHome
				+ "/conf/site-manager/site-manager.config"),
				this.configProperties);
		PropertiesHelper.loadConfigFile(new File(this.crawlerHome
		        + "/conf/crawler.config"), this.configProperties);
		this.jobTimeout = PropertiesHelper.readLong(this.configProperties,
				"crawler.site-manager.job.timeout", this.jobTimeout);
		this.jobTryTime = PropertiesHelper.readInt(this.configProperties,
				"crawler.site-manager.job.tryTime", this.jobTryTime);
		this.queueCleanerPeriod = PropertiesHelper.readLong(
				this.configProperties,
				"crawler.site-manager.queue.cleaner.period",
				this.queueCleanerPeriod);
		this.hadoopUser = PropertiesHelper.readString(this.configProperties,
				"crawler.hadoop.user", System.getProperty("user.name"));
		System.setProperty("HADOOP_USER_NAME", this.hadoopUser);
		this.hadoopURL = PropertiesHelper.readString(this.configProperties,
				"crawler.hadoop.url", null);
		if (this.hadoopURL == null)
		{
			throw new SiteManagerException(
					"config site error!",
					new IllegalArgumentException(
							"crawler.site-manager.backuper.hadoop.url property should not be null!"));
		}
		this.hadoopPath = PropertiesHelper.readString(this.configProperties,
				"crawler.hadoop.path", this.hadoopPath);
		this.backupPeriod = PropertiesHelper.readLong(this.configProperties,
				"crawler.site-manager.backuper.backup.period",
				this.backupPeriod);
		this.zookeeperQuorum = PropertiesHelper.readString(this.configProperties,
				"crawler.zookeeper.quorum", null);
		if (this.zookeeperQuorum == null)
		{
			throw new SiteManagerException(
					"config site error!",
					new IllegalArgumentException(
							"crawler.site-manager.zookeeper.url property should not be null!"));
		}
	}

	private void initWorkDir()
	{
		this.workDir = this.crawlerHome + "/work";
		File workdirFile = new File(this.workDir);
		if (workdirFile.exists())
		{
			IOHelper.deleteFolderContents(workdirFile);
		} else
		{
			workdirFile.mkdirs();
		}
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
