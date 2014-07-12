package guang.crawler.centerConfig;

import guang.crawler.centerConfig.controller.ControllerConfigInfo;
import guang.crawler.centerConfig.siteManagers.SiteManagersConfigInfo;
import guang.crawler.centerConfig.sitesConfig.SitesConfigInfo;
import guang.crawler.centerConfig.workers.WorkersConfigInfo;
import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;

/**
 * 中心配置器,用来对分布式爬虫的各个部分进行配置.之所以称之为中心配置器,是因为它是相对于各个部分本地的配置而言的.
 * 中心配置器的所有内容都存储在Zookeeper中,依靠对Zookeeper树形目录中的各个节点的增删改查以及监控,达到对系统各个部分的控制和协调.
 *
 * @author sun
 *
 */
public class CenterConfig {
	/**
	 * 单例设计模式,全局唯一的静态变量.
	 */
	private static CenterConfig	centerController;
	/**
	 * 爬虫系统在Zookeeper树形目录中的根路径,其他路径都是在此路径之下
	 */
	public static final String	ROOT_PATH	             = "/crawler";
	/**
	 * 爬虫系统全局锁的路径名称
	 */
	public static final String	GLOBAL_CRAWLER_LOCK	     = "/crawler.lock";
	/**
	 * 爬虫系统采集点配置路径的名称
	 */
	public static final String	SITES_CONFIG_PATH	     = "/sites-config";
	/**
	 * 采集点配置中的所有采集点路径
	 */
	public final static String	SITES_PATH	             = "/sites";
	/**
	 * 爬虫系统中的控制器配置路径名称
	 */
	public static final String	CONTROLLER_CONFIG_PATH	 = "/controller-config";
	/**
	 * 爬虫系统中的站点管理器配置路径名称
	 */
	public static final String	SITEMANAGERS_CONFIG_PATH	= "/siteManager-config";
	/**
	 * 所有在线的站点管理器路径名称
	 */
	public static final String	ONLINE_SITEMANAGERS_PATH	= "/online-siteManagers";
	/**
	 * 爬虫系统中worker配置路径名称
	 */
	public static final String	WORKERS_CONFIG_PATH	     = "/workers-config";
	/**
	 * 所有在线的爬虫路径名称
	 */
	public static final String	ONLINE_WORKER_PATH	     = "/online-crawlerWorkers";
	
	/**
	 * 当前类没有构造函数,只能通过该接口获取当前类的单例对象.
	 *
	 * @return
	 */
	public static CenterConfig me() {
		if (CenterConfig.centerController == null) {
			CenterConfig.centerController = new CenterConfig();
		}
		return CenterConfig.centerController;
	}
	
	/**
	 * 用来连接Zookeeper的连接器
	 */
	private ZookeeperConnector	   connector;
	/**
	 * 当前类是否被初始化了.该类只能被初始化一次.
	 */
	private boolean	               inited;
	/**
	 * 采集点配置信息
	 */
	private SitesConfigInfo	       sitesConfigInfo;
	/**
	 * 控制器配置信息
	 */
	private ControllerConfigInfo	controllerInfo;
	
	/**
	 * 站点管理器配置信息
	 */
	private SiteManagersConfigInfo	siteManagersInfo;
	/**
	 * 工作者配置信息
	 */
	private WorkersConfigInfo	   workersInfo;
	
	private CenterConfig() {
	}
	
	/**
	 * 清除整个目录树
	 *
	 * @return
	 */
	public boolean clear() {
		try {
			boolean success = this.connector.recursiveDelete(CenterConfig.ROOT_PATH,
			                                                 null);
			return success;
		} catch (InterruptedException e) {
			return false;
		}
	}
	
	/**
	 * 获取Zookeeper树形目录中控制器的信息
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
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

	/**
	 * 获取Zookeeper树形目录中站点管理器的配置信息
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
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

	/**
	 * 获取Zookeeper树形目录中采集点配置信息
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public SitesConfigInfo getSitesConfigInfo() throws InterruptedException,
	        IOException {
		if (this.sitesConfigInfo == null) {
			this.sitesConfigInfo = new SitesConfigInfo(CenterConfig.ROOT_PATH
			        + CenterConfig.SITES_CONFIG_PATH, this.connector);
			this.sitesConfigInfo.load();
		}
		return this.sitesConfigInfo;
	}
	
	/**
	 * 获取Zookeeper树形目录中爬虫工作者的配置信息
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public WorkersConfigInfo getWorkersInfo() throws InterruptedException,
	        IOException {
		if (this.workersInfo == null) {
			this.workersInfo = new WorkersConfigInfo(CenterConfig.ROOT_PATH
			        + CenterConfig.WORKERS_CONFIG_PATH, this.connector);
			this.workersInfo.load();
		}
		return this.workersInfo;
	}
	
	/**
	 * 初始化当前类,具体工作如下:尝试连接Zookeeper,并获取中央配置的全局锁,如果获得锁,那么尝试初始化配置的路径,将中央配置的骨架结构建立起来
	 * .
	 *
	 * @param zookeeperQuorum
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public CenterConfig init(final String zookeeperQuorum) throws IOException,
	        InterruptedException {
		if (this.inited) {
			return this;
		}
		this.connector = new ZookeeperConnector(zookeeperQuorum);
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
	 * 初始化整个控制树,创建所需的各种节点
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean initPath() throws InterruptedException, IOException {
		
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH,
		                                  CreateMode.PERSISTENT,
		                                  "comment=crawler config node".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
		                                          + CenterConfig.SITES_CONFIG_PATH,
		                                  CreateMode.PERSISTENT,
		                                  "comment=config the crawler".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
		                                          + CenterConfig.SITES_CONFIG_PATH
		                                          + CenterConfig.SITES_PATH,
		                                  CreateMode.PERSISTENT,
		                                  "comment=config the sites".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
		                                          + CenterConfig.SITEMANAGERS_CONFIG_PATH,
		                                  CreateMode.PERSISTENT,
		                                  "comment=config all site managers".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
		                                          + CenterConfig.SITEMANAGERS_CONFIG_PATH
		                                          + CenterConfig.ONLINE_SITEMANAGERS_PATH,
		                                  CreateMode.PERSISTENT,
		                                  "comment=config all site manager or that want to be site manager".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
		                                          + CenterConfig.CONTROLLER_CONFIG_PATH,
		                                  CreateMode.PERSISTENT,
		                                  "comment=config the controller".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
		                                          + CenterConfig.WORKERS_CONFIG_PATH,
		                                  CreateMode.PERSISTENT,
		                                  "comment=config the workers".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
		                                          + CenterConfig.WORKERS_CONFIG_PATH
		                                          + CenterConfig.ONLINE_WORKER_PATH,
		                                  CreateMode.PERSISTENT,
		                                  "comment=config the workers".getBytes());
		this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
		                                          + CenterConfig.WORKERS_CONFIG_PATH
		                                          + CenterConfig.ONLINE_WORKER_PATH
		                                          + "/refresh",
		                                  CreateMode.PERSISTENT,
		                                  "comment=notify the worker to refresh the site manager list".getBytes());
		return true;
	}
	
	/**
	 * 对中央配置的全局锁进行锁定
	 *
	 * @return
	 * @throws InterruptedException
	 */
	public boolean lockGlobal() throws InterruptedException {
		String realPath = this.connector.createNode(CenterConfig.GLOBAL_CRAWLER_LOCK,
		                                            CreateMode.EPHEMERAL,
		                                            "lock the crawler".getBytes());
		return realPath != null;
	}
	
	/**
	 * 关闭中央配置器,关闭底层连接
	 *
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException {
		this.connector.shutdown();
		this.inited = false;
	}
	
	/**
	 * 接触对中央配置全局锁的锁定
	 * 
	 * @param path
	 * @return
	 * @throws InterruptedException
	 */
	public boolean unLockGlobal(final String path) throws InterruptedException {
		return this.connector.simpleDelete(path, null);
	}
}
