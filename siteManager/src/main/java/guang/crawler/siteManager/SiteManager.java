package guang.crawler.siteManager;

import guang.crawler.core.WebURL;
import guang.crawler.jsonServer.AcceptJsonServer;
import guang.crawler.jsonServer.JsonServer;
import guang.crawler.jsonServer.ServerStartException;
import guang.crawler.siteManager.docid.DocidServer;
import guang.crawler.siteManager.jobQueue.JEQueue;
import guang.crawler.siteManager.jobQueue.JEQueueElementTransfer;
import guang.crawler.siteManager.jobQueue.MapQueue;
import guang.crawler.siteManager.jobQueue.WebURLTransfer;

import java.io.File;

public class SiteManager
{
	public static SiteManager getSiteManager() throws SiteManagerException
	{
		if (SiteManager.siteManager == null)
		{
			throw new SiteManagerException(
			        "Site manager should be inited first.");
		}
		return SiteManager.siteManager;
	}
	
	public static void init(SiteConfig siteConfig) throws Exception
	{
		if (SiteManager.siteManager == null)
		{
			SiteManager.siteManager = new SiteManager(siteConfig);
		}
	}
	
	private MapQueue<WebURL>	toDoTaskList;
	private MapQueue<WebURL>	workingTaskList;
	private MapQueue<WebURL>	finishedTaskList;
	private MapQueue<WebURL>	failedTaskList;
	private SiteConfig	       siteConfig;
	
	private DocidServer	       docidServer;
	private JsonServer	       server;
	
	private static SiteManager	siteManager;
	
	private SiteManager(SiteConfig siteConfig) throws Exception
	{
		this.siteConfig = siteConfig;
		this.initJSONServer(siteConfig);
		siteConfig.getSiteToHandle().setSiteManager(
		        this.server.getAddress().getHostAddress() + ":"
		                + this.server.getPort());
		this.docidServer = new DocidServer();
		this.initJobQueue();
		
	}
	
	public DocidServer getDocidServer()
	{
		return this.docidServer;
	}
	
	public MapQueue<WebURL> getFailedTaskList()
	{
		return this.failedTaskList;
	}
	
	public MapQueue<WebURL> getFinishedTaskList()
	{
		return this.finishedTaskList;
	}
	
	public MapQueue<WebURL> getToDoTaskList()
	{
		return this.toDoTaskList;
	}
	
	public MapQueue<WebURL> getWorkingTaskList()
	{
		return this.workingTaskList;
	}
	
	private void initJobQueue() throws Exception
	{
		JEQueueElementTransfer<WebURL> transfer = new WebURLTransfer();
		this.toDoTaskList = new JEQueue<>(this.siteConfig.getBaseDir(), "todo",
		        false, transfer);
		this.finishedTaskList = new JEQueue<>(this.siteConfig.getBaseDir(),
		        "finished", false, transfer);
		this.workingTaskList = new JEQueue<>(this.siteConfig.getBaseDir(),
		        "working", false, transfer);
		this.failedTaskList = new JEQueue<>(this.siteConfig.getBaseDir(),
		        "failed", false, transfer);
		long toDoCount = this.toDoTaskList.getLength();
		if (toDoCount == 0) // 添加种子链接
		{
			String seed = this.siteConfig.getSiteToHandle().getSeedSite();
			WebURL url = new WebURL();
			url.setURL(seed);
			url.setDepth((short) 1);
			url.setDocid(this.docidServer.next());
			url.setSiteManagerName(this.siteConfig.getSiteID());
			this.toDoTaskList.put(url);
			
		}
	}
	
	private void initJSONServer(SiteConfig siteConfig)
	{
		String configFileName = Main.class.getResource("/commandlet.xml")
		        .getPath();
		File configFile = new File(configFileName);
		String schemaFileName = Main.class.getResource("/site.xsd").getPath();
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
		this.finishedTaskList.close();
		this.toDoTaskList.close();
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
