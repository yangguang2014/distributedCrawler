package guang.crawler.siteManager;

import guang.crawler.core.WebURL;
import guang.crawler.siteManager.docid.DocidServer;
import guang.crawler.siteManager.docid.SimpleIncretmentDocidServer;
import guang.crawler.siteManager.jobQueue.JEQueue;
import guang.crawler.siteManager.jobQueue.JEQueueElementTransfer;
import guang.crawler.siteManager.jobQueue.MapQueue;
import guang.crawler.siteManager.jobQueue.WebURLTransfer;
import guang.crawler.siteManager.jsonServer.AcceptJsonServer;
import guang.crawler.siteManager.jsonServer.JsonServer;
import guang.crawler.siteManager.jsonServer.ServerStartException;

import java.io.File;
import java.net.InetAddress;
import java.util.Timer;

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

	private MapQueue<WebURL>	toDoTaskList;
	private MapQueue<WebURL>	workingTaskList;
	private MapQueue<WebURL>	failedTaskList;
	private QueueCleanner	   cleanner;
	private SiteConfig	       siteConfig;
	
	private DocidServer	       docidServer;
	private JsonServer	       server;
	
	private static SiteManager	siteManager;
	
	private Timer	           siteManagerTimer;

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
	
	public MapQueue<WebURL> getWorkingTaskList()
	{
		return this.workingTaskList;
	}
	
	public SiteManager init() throws SiteManagerException
	{
		try
		{
			this.siteConfig = SiteConfig.me();
			this.initJSONServer(this.siteConfig);
			this.siteConfig.getSiteToHandle().setSiteManager(
					InetAddress.getLocalHost().getCanonicalHostName() + ":"
							+ this.server.getPort());
			this.docidServer = new SimpleIncretmentDocidServer();
			this.initJobQueue();
			return this;
		} catch (Exception e)
		{
			throw new SiteManagerException(
			        "Site manager should be inited first.");
		}
		
	}
	
	private void initJobQueue() throws Exception
	{
		JEQueueElementTransfer<WebURL> transfer = new WebURLTransfer();
		this.toDoTaskList = new JEQueue<>(this.siteConfig.getWorkDir(), "todo",
		        false, transfer);
		this.workingTaskList = new JEQueue<>(this.siteConfig.getWorkDir(),
		        "working", false, transfer);
		this.failedTaskList = new JEQueue<>(this.siteConfig.getWorkDir(),
		        "failed", false, transfer);
		long toDoCount = this.toDoTaskList.getLength();
		if (toDoCount == 0) // 添加种子链接
		{
			String seed = this.siteConfig.getSiteToHandle().getSeedSite();
			WebURL url = new WebURL();
			url.setURL(seed);
			url.setDepth((short) 1);
			url.setSiteManagerName(this.siteConfig.getSiteID());
			url.setDocid(this.docidServer.next(url));
			this.toDoTaskList.put(url);
		}
		this.siteConfig.getCrawlerController();
		this.cleanner = new QueueCleanner(this.workingTaskList,
		        this.toDoTaskList, this.failedTaskList,
		        this.siteConfig.getJobTimeout(),
		        this.siteConfig.getJobTryTime());
		this.siteManagerTimer = new Timer(true);
		this.siteManagerTimer.schedule(this.cleanner,
		        this.siteConfig.getJobTimeout(),
		        this.siteConfig.getQueueCleanerPeriod());
	}
	
	private void initJSONServer(SiteConfig siteConfig)
	{
		String configFileName = siteConfig.getCrawlerHome()
		        + "/conf/site-manager/commandlet.xml";
		File configFile = new File(configFileName);
		String schemaFileName = siteConfig.getCrawlerHome()
		        + "/etc/xsd/site.xsd";
		File schemaFile = new File(schemaFileName);
		try
		{
			this.server = new AcceptJsonServer(siteConfig.getListenPort(), 10,
			        2, configFile, schemaFile);
		} catch (ServerStartException e)
		{
			System.out.println("[Failed] server created failed!");
			e.printStackTrace();
		}
	}
	
	public boolean isShutdown()
	{
		if (this.server.isShutdown())
		{
			return true;
		}
		return false;
	}
	
	public void shutdown()
	{
		this.server.shutdown();
		this.toDoTaskList.close();
		this.workingTaskList.close();
		this.failedTaskList.close();
	}
	
	public void start()
	{
		System.out.println("[INFO] Starting site manager ....");
		this.server.start();
		System.out.println("[SUCC] Starting JSON Server success.");
		MonitorThread moitorThread = new MonitorThread(this);
		moitorThread.start();
	}
	
}
