package guang.crawler.siteManager;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.siteManagers.SiteManagerInfo;
import guang.crawler.centerController.sitesConfig.SiteInfo;
import guang.crawler.localConfig.LocalConfig;
import guang.crawler.util.PropertiesHelper;

import java.io.File;

public class SiteConfig extends LocalConfig
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
	 * 当前节点是否被分配了爬取的站点
	 */
	private boolean	        dispatched	        = false;
	/**
	 * 采集点
	 */
	private SiteInfo	    siteToHandle;
	
	/**
	 * 当前站点管理器的工作目录
	 */
	private String	        workDir;
	
	/**
	 * 作业超时的时间
	 */
	private long	        jobTimeout	        = 5 * 60 * 1000;
	
	/**
	 * 作业重试的次数
	 */
	private int	            jobTryTime	        = 3;
	/**
	 * 工作队列的清理时间间隔
	 */
	private long	        queueCleanerPeriod	= 100000;
	
	private boolean	        backTime	        = false;
	
	/**
	 * 备份的版本号
	 */
	private int	            backupVersion;
	
	/**
	 * 备份的时间，默认设置为1个小时
	 */
	private long	        backupPeriod	    = 3600000;
	
	private SiteManagerInfo	siteManagerInfo;
	
	/**
	 * json server的线程数量
	 */
	private int	            jsonserverThreadNum	= 20;
	
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
	
	@Override
	protected String[] getConfigResources()
	{
		return new String[] { "/conf/site-manager/site-manager.config" };
	}
	
	public long getJobTimeout()
	{
		return this.jobTimeout;
	}
	
	public int getJobTryTime()
	{
		return this.jobTryTime;
	}
	
	public int getJsonserverThreadNum()
	{
		return this.jsonserverThreadNum;
	}
	
	public long getQueueCleanerPeriod()
	{
		return this.queueCleanerPeriod;
	}
	
	public SiteManagerInfo getSiteManagerInfo()
	{
		return this.siteManagerInfo;
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
		this.initWorkDir();
		try
		{
			CenterConfig.me().init(this.getZookeeperQuorum());
		} catch (Exception e)
		{
			throw new SiteManagerException("No site to handle", e);
		}
		return this;
	}
	
	@Override
	protected void initProperties()
	{
		super.initProperties();
		this.jobTimeout = PropertiesHelper.readLong(this.configProperties,
		        "crawler.site-manager.job.timeout", this.jobTimeout);
		this.jobTryTime = PropertiesHelper.readInt(this.configProperties,
		        "crawler.site-manager.job.tryTime", this.jobTryTime);
		this.queueCleanerPeriod = PropertiesHelper.readLong(
		        this.configProperties,
		        "crawler.site-manager.queue.cleaner.period",
		        this.queueCleanerPeriod);
		this.backupPeriod = PropertiesHelper.readLong(this.configProperties,
		        "crawler.site-manager.backuper.backup.period",
		        this.backupPeriod);
		this.jsonserverThreadNum = PropertiesHelper.readInt(
		        this.configProperties,
		        "crawler.site-manager.jsonserver.threadNum",
		        this.jsonserverThreadNum);
	}
	
	private void initWorkDir()
	{
		this.workDir = this.getCrawlerHome() + "/work";
		File workdirFile = new File(this.workDir);
		workdirFile.mkdirs();
	}
	
	public boolean isBackupTime()
	{
		return this.backTime;
	}
	
	public boolean isDispatched()
	{
		return this.dispatched;
	}
	
	public void setBackTime(boolean backTime)
	{
		this.backTime = backTime;
	}
	
	public void setBackupVersion(int backupVersion)
	{
		this.backupVersion = backupVersion;
	}
	
	public void setDispatched(boolean isDispatched)
	{
		this.dispatched = isDispatched;
	}
	
	public void setSiteManagerInfo(SiteManagerInfo siteManagerInfo)
	{
		this.siteManagerInfo = siteManagerInfo;
	}
	
	public void setSiteToHandle(SiteInfo siteToHandle)
	{
		this.siteToHandle = siteToHandle;
	}
	
}
