package guang.crawler.centerController;

import guang.crawler.centerController.config.SitesConfigInfo;
import guang.crawler.centerController.controller.ControllerConfigInfo;
import guang.crawler.centerController.siteManagers.SiteManagersConfigInfo;
import guang.crawler.centerController.workers.WorkersConfigInfo;
import guang.crawler.connector.CenterConfigConnector;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;

public class CenterConfig {
	private static CenterConfig centerController;

	public static final String GLOBAL_CRAWLER_LOCK = "/crawler.lock";
	public static final String ROOT_PATH = "/crawler";
	public static final String SITES_CONFIG_PATH = "/sites-config";
	public final static String SITES_PATH = "/sites";
	public static final String CONTROLLER_CONFIG_PATH = "/controller-config";
	public static final String SITEMANAGERS_CONFIG_PATH = "/siteManager-config";
	public static final String ONLINE_SITEMANAGERS_PATH = "/online-siteManagers";
	public static final String WORKERS_CONFIG_PATH = "/workers-config";
	public static final String ONLINE_WORKER_PATH = "/online-crawlerWorkers";

	public static CenterConfig me() {
		if (CenterConfig.centerController == null) {
			CenterConfig.centerController = new CenterConfig();
		}
		return CenterConfig.centerController;
	}

	private SitesConfigInfo configInfo;
	private CenterConfigConnector connector;
	private ControllerConfigInfo controllerInfo;
	private boolean inited;
	private SiteManagersConfigInfo siteManagersInfo;
	private WorkersConfigInfo workersInfo;

	private CenterConfig() {
	}

	/**
	 * 清除整个目录树
	 * 
	 * @return
	 */
	public boolean clear() {
		try {
			boolean success = this.connector.recursiveDelete(
					CenterConfig.ROOT_PATH, null);
			return success;
		} catch (InterruptedException e) {
			return false;
		}
	}

	public ControllerConfigInfo getControllerInfo()
			throws InterruptedException, IOException {
		if (this.controllerInfo == null) {
			this.controllerInfo = new ControllerConfigInfo(
					CenterConfig.ROOT_PATH
							+ CenterConfig.CONTROLLER_CONFIG_PATH,
					this.connector);
			this.controllerInfo.load();
		}
		return this.controllerInfo;
	}

	public SiteManagersConfigInfo getSiteManagersConfigInfo()
			throws InterruptedException, IOException {
		if (this.siteManagersInfo == null) {
			this.siteManagersInfo = new SiteManagersConfigInfo(
					CenterConfig.ROOT_PATH
							+ CenterConfig.SITEMANAGERS_CONFIG_PATH,
					this.connector);
			this.siteManagersInfo.load();
		}
		return this.siteManagersInfo;
	}

	public SitesConfigInfo getSitesConfigInfo() throws InterruptedException,
			IOException {
		if (this.configInfo == null) {
			this.configInfo = new SitesConfigInfo(CenterConfig.ROOT_PATH
					+ CenterConfig.SITES_CONFIG_PATH, this.connector);
			this.configInfo.load();
		}
		return this.configInfo;
	}

	public WorkersConfigInfo getWorkersInfo() throws InterruptedException,
			IOException {
		if (this.workersInfo == null) {
			this.workersInfo = new WorkersConfigInfo(CenterConfig.ROOT_PATH
					+ CenterConfig.WORKERS_CONFIG_PATH, this.connector);
			this.workersInfo.load();
		}
		return this.workersInfo;
	}

	public CenterConfig init(String zookeeperQuorum) throws IOException,
			InterruptedException {
		if (this.inited) {
			return this;
		}
		this.connector = new CenterConfigConnector(zookeeperQuorum);
		if (this.lockGlobal()) {
			try {
				this.initPath();
			} finally {
				this.unLockGlobal(CenterConfig.GLOBAL_CRAWLER_LOCK);
			}
		}
		this.inited = true;
		return this;
	}

	/**
	 * 初始化整个控制树
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean initPath() throws InterruptedException, IOException {

		this.connector
				.checkAndCreateNode(CenterConfig.ROOT_PATH,
						CreateMode.PERSISTENT,
						"comment=crawler config node".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
				+ CenterConfig.SITES_CONFIG_PATH, CreateMode.PERSISTENT,
				"comment=config the crawler".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
				+ CenterConfig.SITES_CONFIG_PATH + CenterConfig.SITES_PATH,
				CreateMode.PERSISTENT, "comment=config the sites".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
				+ CenterConfig.SITEMANAGERS_CONFIG_PATH, CreateMode.PERSISTENT,
				"comment=config all site managers".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
				+ CenterConfig.SITEMANAGERS_CONFIG_PATH
				+ CenterConfig.ONLINE_SITEMANAGERS_PATH, CreateMode.PERSISTENT,
				"comment=config all site manager or that want to be site manager"
						.getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
				+ CenterConfig.CONTROLLER_CONFIG_PATH, CreateMode.PERSISTENT,
				"comment=config the controller".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
				+ CenterConfig.WORKERS_CONFIG_PATH, CreateMode.PERSISTENT,
				"comment=config the workers".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
				+ CenterConfig.WORKERS_CONFIG_PATH
				+ CenterConfig.ONLINE_WORKER_PATH, CreateMode.PERSISTENT,
				"comment=config the workers".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
				+ CenterConfig.WORKERS_CONFIG_PATH
				+ CenterConfig.ONLINE_WORKER_PATH + "/refresh",
				CreateMode.PERSISTENT,
				"comment=notify the worker to refresh the site manager list"
						.getBytes());
		return true;
	}

	public boolean lockGlobal() throws InterruptedException {
		String realPath = this.connector.createNode(
				CenterConfig.GLOBAL_CRAWLER_LOCK, CreateMode.EPHEMERAL,
				"lock the crawler".getBytes());
		return realPath != null;
	}

	public void shutdown() throws InterruptedException {
		this.connector.shutdown();
	}

	public boolean unLockGlobal(String path) throws InterruptedException {
		return this.connector.simpleDelete(path, null);
	}
}
