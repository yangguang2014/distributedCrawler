package guang.crawler.crawlWorker;

import guang.crawler.controller.CrawlerController;
import guang.crawler.util.PropertiesHelper;

import java.io.File;
import java.util.Properties;

public class WorkerConfig
{
	private static WorkerConfig	config;

	public static WorkerConfig me()
	{
		if (WorkerConfig.config == null)
		{
			WorkerConfig.config = new WorkerConfig();
		}
		return WorkerConfig.config;
	}
	
	/**
	 * 爬虫所代表的user agent
	 */
	private String	          userAgentString	             = "crawler4j (http://code.google.com/p/crawler4j/)";
	
	/**
	 * 是否爬取HTTPS的页面
	 */
	private boolean	          includeHttpsPages	             = false;
	
	/**
	 * 是否爬取二进制内容
	 */
	private boolean	          includeBinaryContentInCrawling	= false;
	
	/**
	 * 针对每个目标主机，最多允许有多少个连接
	 */
	private int	              maxConnectionsPerHost	         = 100;
	
	/**
	 * 当前主机允许的最大数目的连接数
	 */
	private int	              maxTotalConnections	         = 100;
	
	/**
	 * 套接字超时时长，单位是ms
	 */
	private long	          socketTimeout	                 = 20000;
	
	/**
	 * 连接超时时长，单位是ms
	 */
	private long	          connectionTimeout	             = 30000;
	
	/**
	 * 每个页面抽取的最大数量的出链
	 */
	private int	              maxOutgoingLinksToFollow	     = 5000;
	
	/**
	 * 每个页面最大的下载大小，超过该大小将不进行下载
	 */
	private int	              maxDownloadSize	             = 1048576;
	
	/**
	 * TODO 这个暂时没有考虑到，实际上是应当考虑的。Should we follow redirects?
	 */
	private boolean	          followRedirects	             = true;
	
	/**
	 * 使用代理的主机名
	 */
	private String	          proxyHost	                     = null;
	
	/**
	 * 使用的代理的端口号
	 */
	private int	              proxyPort	                     = 80;
	
	/**
	 * 使用的代理的用户名
	 */
	private String	          proxyUsername	                 = null;
	
	/**
	 * 使用的代理的密码
	 */
	private String	          proxyPassword	                 = null;
	
	/**
	 * 从配置文件加载的属性信息
	 */
	private Properties	      configProperties;
	/**
	 * crawler的home
	 */
	private String	          crawlerHome;
	/**
	 * 爬虫控制器
	 */
	private CrawlerController	crawlerController;
	/**
	 * zookeeper的连接字符串
	 */
	private String	          zookeeperQuorum;

	/**
	 * zookeeper的端口号
	 */
	private int	              zookeeperClientPort	         = 2181;

	private WorkerConfig()
	{
	}

	public long getConnectionTimeout()
	{
		return this.connectionTimeout;
	}

	public CrawlerController getCrawlerController()
	{
		return this.crawlerController;
	}

	public String getCrawlerHome()
	{
		return this.crawlerHome;
	}

	public int getMaxConnectionsPerHost()
	{
		return this.maxConnectionsPerHost;
	}

	public int getMaxDownloadSize()
	{
		return this.maxDownloadSize;
	}

	public int getMaxOutgoingLinksToFollow()
	{
		return this.maxOutgoingLinksToFollow;
	}

	public int getMaxTotalConnections()
	{
		return this.maxTotalConnections;
	}
	
	public String getProxyHost()
	{
		return this.proxyHost;
	}

	public String getProxyPassword()
	{
		return this.proxyPassword;
	}

	public int getProxyPort()
	{
		return this.proxyPort;
	}

	public String getProxyUsername()
	{
		return this.proxyUsername;
	}

	public long getSocketTimeout()
	{
		return this.socketTimeout;
	}

	public String getUserAgentString()
	{
		return this.userAgentString;
	}

	public int getZookeeperClientPort()
	{
		return this.zookeeperClientPort;
	}

	public String getZookeeperQuorum()
	{
		return this.zookeeperQuorum;
	}

	public WorkerConfig init()
	{
		this.crawlerHome = System.getProperty("crawler.home");
		this.initProperties();

		return this;
	}

	private void initProperties()
	{
		this.configProperties = new Properties();
		PropertiesHelper.loadConfigFile(new File(this.crawlerHome
		        + "/conf/site-manager/site-manager.config"),
		        this.configProperties);
		PropertiesHelper.loadConfigFile(new File(this.crawlerHome
				+ "/conf/crawler-worker/crawler-worker.config"),
				this.configProperties);
		this.zookeeperQuorum = PropertiesHelper.readString(
		        this.configProperties, "crawler.zookeeper.quorum", null);
		this.zookeeperClientPort = PropertiesHelper.readInt(
		        this.configProperties, "crawler.zookeeper.clientPort",
		        this.zookeeperClientPort);
		this.includeHttpsPages = PropertiesHelper.readBoolean(
		        this.configProperties, "crawler.worker.include.https",
				this.includeHttpsPages);
		this.includeBinaryContentInCrawling = PropertiesHelper.readBoolean(
		        this.configProperties, "crawler.worker.include.binary",
				this.includeBinaryContentInCrawling);
		this.maxOutgoingLinksToFollow = PropertiesHelper.readInt(
		        this.configProperties,
		        "crawler.worker.page.links.outgoing.max",
				this.maxOutgoingLinksToFollow);
		this.userAgentString = PropertiesHelper.readString(
		        this.configProperties, "crawler.worker.fetcher.userAgent",
		        this.userAgentString);
		this.socketTimeout = PropertiesHelper.readLong(this.configProperties,
				"crawler.worker.fetcher.socket.timeout", this.socketTimeout);
		this.connectionTimeout = PropertiesHelper.readLong(
		        this.configProperties,
				"crawler.worker.fetcher.connection.timeout",
		        this.connectionTimeout);
		this.maxTotalConnections = PropertiesHelper.readInt(
				this.configProperties,
				"crawler.worker.fetcher.totalConnections.max",
				this.maxTotalConnections);
		this.maxConnectionsPerHost = PropertiesHelper.readInt(
				this.configProperties,
				"crawler.worker.fetcher.connectionsPerHost.max",
				this.maxConnectionsPerHost);
		this.maxDownloadSize = PropertiesHelper.readInt(this.configProperties,
				"crawler.worker.fetcher.downloadSizePerPage.max",
				this.maxDownloadSize);
		this.proxyHost = PropertiesHelper.readString(this.configProperties,
				"crawler.worker.fetcher.proxy.host", this.proxyHost);
		this.proxyPort = PropertiesHelper.readInt(this.configProperties,
				"crawler.worker.fetcher.proxy.port", this.proxyPort);
		this.proxyUsername = PropertiesHelper.readString(this.configProperties,
				"crawler.worker.fetcher.proxy.user", this.proxyUsername);
		this.proxyPassword = PropertiesHelper.readString(this.configProperties,
				"crawler.worker.fetcher.proxy.password", this.proxyPassword);

	}

	public boolean isIncludeBinaryContentInCrawling()
	{
		return this.includeBinaryContentInCrawling;
	}

	public boolean isIncludeHttpsPages()
	{
		return this.includeHttpsPages;
	}

	public void setCrawlerController(CrawlerController crawlerController)
	{
		this.crawlerController = crawlerController;
	}

	public void setCrawlerHome(String crawlerHome)
	{
		this.crawlerHome = crawlerHome;
	}

}
