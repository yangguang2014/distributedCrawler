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

/**
 * 当前类是站点管理器,用来启动和关闭站点管理器
 *
 * @author sun
 *
 */
public class SiteManager {
	public static SiteManager me() {
		if (SiteManager.siteManager == null) {
			SiteManager.siteManager = new SiteManager();
		}
		return SiteManager.siteManager;
	}
	
	/**
	 * 当前采集点正在运行
	 */
	private boolean	            running	= false;
	/**
	 * 下一步将要爬取的URL列表
	 */
	private MapQueue<WebURL>	toDoTaskList;
	/**
	 * 正在爬取的URL列表
	 */
	private MapQueue<WebURL>	workingTaskList;
	/**
	 * 爬取失败的URL列表
	 */
	private MapQueue<WebURL>	failedTaskList;
	/**
	 * 定时对列表进行清理的后台线程
	 */
	private QueueCleannerDaemon	cleannerDaemon;
	/**
	 * 定时对站点管理器进行备份的后台线程
	 */
	private SiteBackupDaemon	backuperDaemon;
	/**
	 * 当前站点的本地配置信息
	 */
	private SiteConfig	        siteConfig;
	/**
	 * 用来产生文档ID的服务
	 */
	private DocidServer	        docidServer;
	/**
	 * 站点管理器启动的json服务器.爬虫工作者通过与该服务器通信完成各项业务.
	 */
	private JsonServer	        jsonServer;
	/**
	 * 当前类的单例对象
	 */
	private static SiteManager	siteManager;
	/**
	 * 用来过滤重复网页的过滤器
	 */
	private ObjectFilter	    urlsFilter;
	/**
	 * 站点管理器维护的定时器,所有的定时服务都应当绑定到该定时器中,不应当再单独创建定时器了.
	 */
	private Timer	            siteManagerTimer;
	/**
	 * 用来监控中央配置器中当前站点管理器的节点的后台线程.
	 */
	private Thread	            siteManagerWatcherDaemon;
	/**
	 * 中央配置器
	 */
	private CenterConfig	    centerConfig;
	
	private SiteManager() {
	}
	
	/**
	 * 获取定时备份后台线程
	 *
	 * @return
	 */
	public SiteBackupDaemon getBackuperDaemon() {
		return this.backuperDaemon;
	}

	/**
	 * 获取产生文档ID的服务
	 *
	 * @return
	 */
	public DocidServer getDocidServer() {
		return this.docidServer;
	}

	/**
	 * 获取失败的URL列表
	 *
	 * @return
	 */
	public MapQueue<WebURL> getFailedTaskList() {
		return this.failedTaskList;
	}

	/**
	 * 获取将要爬取的URL列表
	 *
	 * @return
	 */
	public MapQueue<WebURL> getToDoTaskList() {
		return this.toDoTaskList;
	}

	/**
	 * 获取URL过滤器
	 *
	 * @return
	 */
	public ObjectFilter getUrlsFilter() {
		return this.urlsFilter;
	}

	/**
	 * 获取正在处理的URL列表
	 *
	 * @return
	 */
	public MapQueue<WebURL> getWorkingTaskList() {
		return this.workingTaskList;
	}
	
	/**
	 * 初始化工作，读取配置文件，初始化中央管理器
	 *
	 * @return
	 * @throws SiteManagerException
	 */
	public SiteManager init() throws SiteManagerException {
		try {
			this.siteConfig = SiteConfig.me()
			                            .init();
			this.centerConfig = CenterConfig.me()
			                                .init(this.siteConfig.getZookeeperQuorum());
			
			return this;
		} catch (Exception e) {
			throw new SiteManagerException("Site manager inited failed.", e);
		}
		
	}
	
	/**
	 * 初始化工作队列,创建相应的目录和数据结构
	 *
	 * @throws Exception
	 */
	private void initJobQueue() throws Exception {
		// 每个不同的siteManager都有其自身的工作目录
		File envHome = new File(this.siteConfig.getWorkDir() + "/"
		        + SiteConfig.me()
		                    .getSiteManagerInfo()
		                    .getSiteToHandle() + "/je-queues");
		if (envHome.exists()) {
			IOHelper.deleteFolderContents(envHome);
		}
		if (!envHome.exists()) {
			if (!envHome.mkdirs()) {
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
	
	/**
	 * 初始化JSON 服务器,JSON服务器将被启动
	 *
	 * @throws InterruptedException
	 * @throws SiteManagerException
	 */
	private void initJSONServer() throws InterruptedException,
	        SiteManagerException {
		String configFileName = this.siteConfig.getCrawlerHome()
		        + "/conf/site-manager/commandlet.xml";
		File configFile = new File(configFileName);
		String schemaFileName = this.siteConfig.getCrawlerHome()
		        + "/etc/xsd/components.xsd";
		File schemaFile = new File(schemaFileName);
		try {
			this.jsonServer = new AcceptJsonServer(0, 10,
			        this.siteConfig.getJsonserverThreadNum(), configFile,
			        schemaFile);
			try {
				this.siteConfig.getSiteManagerInfo()
				               .setManagerAddress(NetworkHelper.getIPAddress()
				                                          + ":"
				                                          + this.jsonServer.getPort(),
				                                  true);
			} catch (UnknownHostException e) {
				throw new SiteManagerException(
				        "can not regist the json server", e);
			} catch (IOException e) {
				throw new SiteManagerException(
				        "can not regist the json server", e);
			} catch (KeeperException e) {
				throw new SiteManagerException(
				        "can not regist the json server", e);
			}
			
		} catch (ServerStartException e) {
			System.out.println("[Failed] server created failed!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 当前站点管理器是否被关闭了.
	 *
	 * @return
	 */
	public boolean isShutdown() {
		if (this.jsonServer.isShutdown()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 装载工作队列.
	 * <p>
	 * 首先检查是否有备份的数据,如果有备份的数据,那么加载备份数据.然后将种子站点加入进去.
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void loadWorkQueue() throws IOException, InterruptedException {
		// 首先加载备份的数据
		boolean backed = this.backuperDaemon.loadBackupData();
		if (backed) {
			// 将failed list和 working list中的数据重新加载到todo list中
			this.backuperDaemon.rescheduleTaskList(this.workingTaskList);
			this.backuperDaemon.rescheduleTaskList(this.failedTaskList);
		}
		// 将种子站点添加到todo List中
		String seedsString = this.siteConfig.getSiteToHandle()
		                                    .getWebGatherNodeInfo()
		                                    .getWgnEntryUrl()
		                                    .trim();
		String seeds[] = seedsString.split(",");
		for (String seed : seeds) {
			seed = seed.trim();
			if (seed.equals("")) {
				continue;
			}
			WebURL url = WebURL.newWebURL()
			                   .setURL(seed)
			                   .setDepth((short) 1)
			                   .setSiteManagerId(this.siteConfig.getSiteManagerInfo()
			                                                    .getSiteManagerId())
			                   .setSiteId(this.siteConfig.getSiteToHandle()
			                                             .getSiteId());
			url.setDocid(this.docidServer.next(url));
			this.toDoTaskList.put(url);
		}

	}
	
	/**
	 * 启动站点管理器，主要是注册一个站点管理器角色，然后监听zookeeper的消息，作出相应的操作。
	 */
	public void start() {
		SiteManagerInfo managerInfo = null;
		try {
			managerInfo = this.centerConfig.getSiteManagersConfigInfo()
			                               .getOnlineSiteManagers()
			                               .registSiteManager();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error to start site manager: regist site manager failed.");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error to start site manager: regist site manager failed.");
			return;
		} catch (KeeperException e) {
			e.printStackTrace();
			System.out.println("Error to start site manager: regist site manager failed.");
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
	private void startDaemon() {
		this.siteManagerTimer = new Timer(true);
		this.siteManagerTimer.schedule(this.cleannerDaemon,
		                               this.siteConfig.getJobTimeout(),
		                               this.siteConfig.getQueueCleanerPeriod());
		this.siteManagerTimer.schedule(this.backuperDaemon,
		                               this.siteConfig.getBackupPeriod(),
		                               this.siteConfig.getBackupPeriod());
		this.jsonServer.start();
	}
	
	/**
	 * 开始爬取信息
	 * 
	 * @throws Exception
	 */
	public synchronized void startGathering() throws Exception {
		if (!this.running) {
			// 1. 初始化工作队列
			this.initJobQueue();
			this.urlsFilter = BitMapFilter.newFilter();
			// 2. 初始化相关后台线程
			this.initJSONServer();
			this.docidServer = new MD5UrlDocidServer();
			this.cleannerDaemon = QueueCleannerDaemon.newDaemon();
			this.backuperDaemon = SiteBackupDaemon.newDaemon()
			                                      .init();
			// 3. 加载备份数据
			this.loadWorkQueue();
			// 4. 启动这些后台线程
			System.out.println("[INFO] Starting site manager ....");
			this.startDaemon();
			System.out.println("[SUCC] Starting JSON Server success.");
			this.running = true;
		}
		
	}
	
	/**
	 * 关闭所有的后台线程
	 */
	private void stopDaemon() {
		this.siteManagerTimer.cancel();
		this.jsonServer.shutdown();
		this.jsonServer.waitForStop();
	}
	
	/**
	 * 关闭站点管理器 关闭后台线程，然后强制进行一次备份
	 */
	public synchronized void stopGathering() {
		if (this.running) {
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
