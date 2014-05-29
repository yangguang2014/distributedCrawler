package guang.crawler.siteManager;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.siteManagers.SiteManagerInfo;
import guang.crawler.core.WebURL;
import guang.crawler.jsonServer.AcceptJsonServer;
import guang.crawler.jsonServer.JsonServer;
import guang.crawler.jsonServer.ServerStartException;
import guang.crawler.siteManager.docid.DocidServer;
import guang.crawler.siteManager.docid.SimpleIncretmentDocidServer;
import guang.crawler.siteManager.jobQueue.JEQueue;
import guang.crawler.siteManager.jobQueue.JEQueueElementTransfer;
import guang.crawler.siteManager.jobQueue.MapQueue;
import guang.crawler.siteManager.jobQueue.MapQueueIteraotr;
import guang.crawler.siteManager.jobQueue.WebURLTransfer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class SiteManager implements Watcher {
	public static SiteManager me() {
		if (SiteManager.siteManager == null) {
			SiteManager.siteManager = new SiteManager();
		}
		return SiteManager.siteManager;
	}

	private MapQueue<WebURL> toDoTaskList;
	private MapQueue<WebURL> workingTaskList;
	private MapQueue<WebURL> failedTaskList;
	private QueueCleanner cleanner;
	private SiteBackuper backuper;
	private SiteConfig siteConfig;
	private DocidServer docidServer;
	private JsonServer server;
	private static SiteManager siteManager;

	private Timer siteManagerTimer;

	private Thread siteManagerWatcher;

	private SiteManager() {
	}

	public DocidServer getDocidServer() {
		return this.docidServer;
	}

	public MapQueue<WebURL> getFailedTaskList() {
		return this.failedTaskList;
	}

	public MapQueue<WebURL> getToDoTaskList() {
		return this.toDoTaskList;
	}

	public MapQueue<WebURL> getWorkingTaskList() {
		return this.workingTaskList;
	}

	public SiteManager init() throws SiteManagerException {

		try {
			this.siteConfig = SiteConfig.me().init();
			CenterConfig.me().init(this.siteConfig.getZookeeperQuorum());
			return this;
		} catch (Exception e) {
			throw new SiteManagerException("Site manager inited failed.", e);
		}

	}

	/**
	 * 初始化一些后台线程，维护系统的运行
	 * 
	 * @throws IOException
	 */
	private void initDaemon() {
		this.siteManagerTimer = new Timer(true);
		this.siteManagerTimer.schedule(this.cleanner,
				this.siteConfig.getJobTimeout(),
				this.siteConfig.getQueueCleanerPeriod());
		this.siteManagerTimer.schedule(this.backuper,
				this.siteConfig.getBackupPeriod(),
				this.siteConfig.getBackupPeriod());

	}

	private void initJobQueue() throws Exception {
		JEQueueElementTransfer<WebURL> transfer = new WebURLTransfer();
		this.toDoTaskList = new JEQueue<>(this.siteConfig.getWorkDir(), "todo",
				false, transfer);
		this.workingTaskList = new JEQueue<>(this.siteConfig.getWorkDir(),
				"working", false, transfer);
		this.failedTaskList = new JEQueue<>(this.siteConfig.getWorkDir(),
				"failed", false, transfer);

	}

	private void initJSONServer() throws InterruptedException {
		String configFileName = this.siteConfig.getCrawlerHome()
				+ "/conf/site-manager/commandlet.xml";
		File configFile = new File(configFileName);
		String schemaFileName = this.siteConfig.getCrawlerHome()
				+ "/etc/xsd/site.xsd";
		File schemaFile = new File(schemaFileName);
		try {
			this.server = new AcceptJsonServer(this.siteConfig.getListenPort(),
					10, 2, configFile, schemaFile);
			try {
				this.siteConfig.getSiteManagerInfo().setManagerAddress(
						InetAddress.getLocalHost().getCanonicalHostName() + ":"
								+ this.server.getPort(), true);
			} catch (IOException | KeeperException e) {
				// Can not happen.
				e.printStackTrace();

			}
		} catch (ServerStartException e) {
			System.out.println("[Failed] server created failed!");
			e.printStackTrace();
		}
	}

	public boolean isShutdown() {
		if (this.server.isShutdown()) {
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
	private void loadBackupData() throws IOException, InterruptedException {
		// 首先加载备份的数据
		boolean backed = SiteBackuper.me().loadBackupData();
		if (backed) {
			// 将failed list和 working list中的数据重新加载到todo list中
			this.rescheduleTaskList(this.workingTaskList);
			this.rescheduleTaskList(this.failedTaskList);
		} else {
			// 将种子站点添加到todo List中
			String[] seeds = this.siteConfig.getSiteToHandle().getSeedSites();
			for (String seed : seeds) {
				if (seed.trim().equals("")) {
					continue;
				}
				WebURL url = new WebURL();
				url.setURL(seed);
				url.setDepth((short) 1);
				url.setSiteManagerName(this.siteConfig.getSiteManagerId());
				url.setDocid(this.docidServer.next(url));
				this.toDoTaskList.put(url);
			}

		}

	}

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub

	}

	private void rescheduleTaskList(MapQueue<WebURL> fromList) {
		if (fromList.getLength() > 0) {
			try (MapQueueIteraotr<WebURL> iterator = fromList.iterator()) {
				while (iterator.hasNext()) {
					this.toDoTaskList.put(iterator.next().resetTryTime());
				}
			}
		}
	}

	public void shutdown() {
		this.server.shutdown();
		this.toDoTaskList.close();
		this.workingTaskList.close();
		this.failedTaskList.close();
	}

	/**
	 * 启动站点管理器，主要是注册一个站点管理器角色，然后监听zookeeper的消息，作出相应的操作。
	 */
	public void start() {
		SiteManagerInfo managerInfo;
		try {
			managerInfo = CenterConfig.me().getSiteManagersConfigInfo()
					.getOnlineSiteManagers().registSiteManager();
			this.siteConfig.setSiteManagerId(managerInfo.getSiteManagerId());
			this.siteConfig.setSiteManagerInfo(managerInfo);
			this.siteManagerWatcher = new Thread(new SiteManagerWatcher(),
					"site-manager-watcher");
			this.siteManagerWatcher.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("[FAILED] Failed to start site manager");
		}
	}

	public void startSiteManager() throws Exception {
		this.initJSONServer();
		this.docidServer = new SimpleIncretmentDocidServer();
		this.initJobQueue();
		this.cleanner = QueueCleanner.me();
		this.backuper = SiteBackuper.me().init();
		this.loadBackupData();
		this.initDaemon();
		System.out.println("[INFO] Starting site manager ....");
		this.server.start();
		System.out.println("[SUCC] Starting JSON Server success.");
		MonitorThread moitorThread = new MonitorThread(this);
		moitorThread.start();
	}

}
