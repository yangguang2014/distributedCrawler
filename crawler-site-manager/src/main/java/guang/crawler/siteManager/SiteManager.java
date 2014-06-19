package guang.crawler.siteManager;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.siteManagers.SiteManagerInfo;
import guang.crawler.commons.WebURL;
import guang.crawler.jsonServer.AcceptJsonServer;
import guang.crawler.jsonServer.JsonServer;
import guang.crawler.jsonServer.ServerStartException;
import guang.crawler.siteManager.daemon.QueueCleannerDaemon;
import guang.crawler.siteManager.daemon.SiteBackupDaemon;
import guang.crawler.siteManager.daemon.SiteManagerWatcherDaemon;
import guang.crawler.siteManager.docid.DocidServer;
import guang.crawler.siteManager.docid.MD5UrlDocidServer;
import guang.crawler.siteManager.jobQueue.JEQueue;
import guang.crawler.siteManager.jobQueue.JEQueueElementTransfer;
import guang.crawler.siteManager.jobQueue.MapQueue;
import guang.crawler.siteManager.jobQueue.WebURLTransfer;
import guang.crawler.siteManager.urlFilter.BitMapFilter;
import guang.crawler.siteManager.urlFilter.ObjectFilter;
import guang.crawler.siteManager.util.IOHelper;
import guang.crawler.util.NetworkHelper;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;

import org.apache.zookeeper.KeeperException;

public class SiteManager
{
	public static SiteManager me()
	{
		if (SiteManager.siteManager == null)
		{
			SiteManager.siteManager = new SiteManager();
		}
		return SiteManager.siteManager;
	}
	
	/**
	 * 当前采集点正在运行
	 */
	private boolean	            running	= false;
	
	private MapQueue<WebURL>	toDoTaskList;
	private MapQueue<WebURL>	workingTaskList;
	private MapQueue<WebURL>	failedTaskList;
	private QueueCleannerDaemon	cleannerDaemon;
	private SiteBackupDaemon	backuperDaemon;
	public SiteBackupDaemon getBackuperDaemon() {
		return backuperDaemon;
	}

	private SiteConfig	        siteConfig;
	private DocidServer	        docidServer;
	private JsonServer	        jsonServer;
	private static SiteManager	siteManager;
	private ObjectFilter	    urlsFilter;
	
	private Timer	            siteManagerTimer;
	
	private Thread	            siteManagerWatcherDaemon;
	
	private CenterConfig	    centerConfig;
	
	private SiteManager()
	{
	}
	
	public DocidServer getDocidServer()
	{
		return this.docidServer;
	}
	
	public MapQueue<WebURL> getFailedTaskList()
	{
		return this.failedTaskList;
	}
	
	public MapQueue<WebURL> getToDoTaskList()
	{
		return this.toDoTaskList;
	}
	
	public ObjectFilter getUrlsFilter()
	{
		return this.urlsFilter;
	}
	
	public MapQueue<WebURL> getWorkingTaskList()
	{
		return this.workingTaskList;
	}
	
	/**
	 * 初始化工作，读取配置文件，初始化中央管理器
	 * 
	 * @return
	 * @throws SiteManagerException
	 */
	public SiteManager init() throws SiteManagerException
	{
		try
		{
			this.siteConfig = SiteConfig.me().init();
			this.centerConfig = CenterConfig.me().init(
			        this.siteConfig.getZookeeperQuorum());
			
			return this;
		} catch (Exception e)
		{
			throw new SiteManagerException("Site manager inited failed.", e);
		}
		
	}
	
	private void initJobQueue() throws Exception
	{
		// 每个不同的siteManager都有其自身的工作目录
		File envHome = new File(this.siteConfig.getWorkDir() + "/"
		        + SiteConfig.me().getSiteManagerInfo().getSiteToHandle()
		        + "/je-queues");
		if (envHome.exists())
		{
			IOHelper.deleteFolderContents(envHome);
		}
		if (!envHome.exists())
		{
			if (!envHome.mkdirs())
			{
				throw new Exception("Couldn't create this folder: "
				        + envHome.getAbsolutePath());
			}
		}
		JEQueueElementTransfer<WebURL> transfer = new WebURLTransfer();
		this.toDoTaskList = new JEQueue<WebURL>(envHome, "todo", false,
		        transfer);
		this.workingTaskList = new JEQueue<WebURL>(envHome, "working", false,
		        transfer);
		this.failedTaskList = new JEQueue<WebURL>(envHome, "failed", false,
		        transfer);
		
	}
	
	private void initJSONServer() throws InterruptedException,
	        SiteManagerException
	{
		String configFileName = this.siteConfig.getCrawlerHome()
		        + "/conf/site-manager/commandlet.xml";
		File configFile = new File(configFileName);
		String schemaFileName = this.siteConfig.getCrawlerHome()
		        + "/etc/xsd/site.xsd";
		File schemaFile = new File(schemaFileName);
		try
		{
			this.jsonServer = new AcceptJsonServer(0, 10,
			        this.siteConfig.getJsonserverThreadNum(), configFile,
			        schemaFile);
			try
			{
				this.siteConfig.getSiteManagerInfo().setManagerAddress(
				        NetworkHelper.getIPAddress() + ":"
				                + this.jsonServer.getPort(), true);
			} catch (UnknownHostException e)
			{
				throw new SiteManagerException(
				        "can not regist the json server", e);
			} catch (IOException e)
			{
				throw new SiteManagerException(
				        "can not regist the json server", e);
			} catch (KeeperException e)
			{
				throw new SiteManagerException(
				        "can not regist the json server", e);
			}
			
		} catch (ServerStartException e)
		{
			System.out.println("[Failed] server created failed!");
			e.printStackTrace();
		}
	}
	
	public boolean isShutdown()
	{
		if (this.jsonServer.isShutdown())
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 加载备份的数据
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void loadWorkQueue() throws IOException, InterruptedException
	{
		// 首先加载备份的数据
		boolean backed = this.backuperDaemon.loadBackupData();
		if (backed)
		{
			// 将failed list和 working list中的数据重新加载到todo list中
			this.backuperDaemon.rescheduleTaskList(this.workingTaskList);
			this.backuperDaemon.rescheduleTaskList(this.failedTaskList);
		}
		// 将种子站点添加到todo List中
		String seedsString = this.siteConfig.getSiteToHandle()
		        .getWebGatherNodeInfo().getWgnEntryUrl().trim();
		String seeds[] = seedsString.split(",");
		for (String seed : seeds)
		{
			seed = seed.trim();
			if (seed.equals(""))
			{
				continue;
			}
			WebURL url = new WebURL();
			url.setURL(seed);
			url.setDepth((short) 1);
			url.setSiteManagerId(this.siteConfig.getSiteManagerInfo()
			        .getSiteManagerId());
			url.setSiteId(this.siteConfig.getSiteToHandle().getSiteId());
			url.setDocid(this.docidServer.next(url));
			this.toDoTaskList.put(url);
		}
		
	
		
	}
	
	/**
	 * 启动站点管理器，主要是注册一个站点管理器角色，然后监听zookeeper的消息，作出相应的操作。
	 */
	public void start()
	{
		SiteManagerInfo managerInfo = null;
		try
		{
			managerInfo = this.centerConfig.getSiteManagersConfigInfo()
			        .getOnlineSiteManagers().registSiteManager();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			System.out
			        .println("Error to start site manager: regist site manager failed.");
			return;
		} catch (IOException e)
		{
			e.printStackTrace();
			System.out
			        .println("Error to start site manager: regist site manager failed.");
			return;
		} catch (KeeperException e)
		{
			e.printStackTrace();
			System.out
			        .println("Error to start site manager: regist site manager failed.");
			return;
		}
		
		this.siteConfig.setSiteManagerInfo(managerInfo);
		this.siteManagerWatcherDaemon = new Thread(
		        new SiteManagerWatcherDaemon(), "site-manager-watcher daemon");
		this.siteManagerWatcherDaemon.start();
	}
	
	/**
	 * 初始化一些后台线程，维护系统的运行
	 * 
	 * @throws IOException
	 */
	private void startDaemon()
	{
		this.siteManagerTimer = new Timer(true);
		this.siteManagerTimer.schedule(this.cleannerDaemon,
		        this.siteConfig.getJobTimeout(),
		        this.siteConfig.getQueueCleanerPeriod());
		this.siteManagerTimer.schedule(this.backuperDaemon,
		        this.siteConfig.getBackupPeriod(),
		        this.siteConfig.getBackupPeriod());
		this.jsonServer.start();
	}
	
	public synchronized void startGathering() throws Exception
	{
		if (!this.running)
		{
			// 1. 初始化工作队列
			this.initJobQueue();
			this.urlsFilter = BitMapFilter.newFilter();
			// 2. 初始化相关后台线程
			this.initJSONServer();
			this.docidServer = new MD5UrlDocidServer();
			this.cleannerDaemon = QueueCleannerDaemon.newDaemon();
			this.backuperDaemon = SiteBackupDaemon.newDaemon().init();
			// 3. 加载备份数据
			this.loadWorkQueue();
			// 4. 启动这些后台线程
			System.out.println("[INFO] Starting site manager ....");
			this.startDaemon();
			System.out.println("[SUCC] Starting JSON Server success.");
			this.running = true;
		}
		
	}
	
	private void stopDaemon()
	{
		this.siteManagerTimer.cancel();
		this.jsonServer.shutdown();
		this.jsonServer.waitForStop();
	}
	
	/**
	 * 关闭站点管理器 关闭后台线程，然后强制进行一次备份
	 */
	public synchronized void stopGathering()
	{
		if (this.running)
		{
			// 1. 关闭所有线程
			this.stopDaemon();
			// 2. 强制进行一次备份
			this.backuperDaemon.forceBackup();
			// 3. 关闭相关数据结构
			this.toDoTaskList.close();
			this.workingTaskList.close();
			this.failedTaskList.close();
			this.running = false;
		}
		
	}
	
}
