package guang.crawler.crawlWorker.fetcher;

public class CrawlConfig
{

	/**
	 * The folder which will be used by crawler for storing the intermediate
	 * crawl data. The content of this folder should not be modified manually.
	 */
	private String	crawlStorageFolder;

	/**
	 * If this feature is enabled, you would be able to resume a previously
	 * stopped/crashed crawl. However, it makes crawling slightly slower
	 */
	private boolean	resumableCrawling				= false;

	/**
	 * Maximum depth of crawling For unlimited depth this parameter should be
	 * set to -1
	 */
	private int		maxDepthOfCrawling				= -1;

	/**
	 * Maximum number of pages to fetch For unlimited number of pages, this
	 * parameter should be set to -1
	 */
	private int		maxPagesToFetch					= -1;

	/**
	 * user-agent string that is used for representing your crawler to web
	 * servers. See http://en.wikipedia.org/wiki/User_agent for more details
	 */
	private String	userAgentString					= "crawler4j (http://code.google.com/p/crawler4j/)";

	/**
	 * Politeness delay in milliseconds (delay between sending two requests to
	 * the same host).
	 */
	private int		politenessDelay					= 200;

	/**
	 * Should we also crawl https pages?
	 */
	private boolean	includeHttpsPages				= false;

	/**
	 * Should we fetch binary content such as images, audio, ...?
	 */
	private boolean	includeBinaryContentInCrawling	= false;

	/**
	 * Maximum Connections per host
	 */
	private int		maxConnectionsPerHost			= 100;

	/**
	 * Maximum total connections
	 */
	private int		maxTotalConnections				= 100;

	/**
	 * Socket timeout in milliseconds
	 */
	private int		socketTimeout					= 20000;

	/**
	 * Connection timeout in milliseconds
	 */
	private int		connectionTimeout				= 30000;

	/**
	 * Max number of outgoing links which are processed from a page
	 */
	private int		maxOutgoingLinksToFollow		= 5000;

	/**
	 * Max allowed size of a page. Pages larger than this size will not be
	 * fetched.
	 */
	private int		maxDownloadSize					= 1048576;

	/**
	 * Should we follow redirects?
	 */
	private boolean	followRedirects					= true;

	/**
	 * If crawler should run behind a proxy, this parameter can be used for
	 * specifying the proxy host.
	 */
	private String	proxyHost						= null;

	/**
	 * If crawler should run behind a proxy, this parameter can be used for
	 * specifying the proxy port.
	 */
	private int		proxyPort						= 80;

	/**
	 * If crawler should run behind a proxy and user/pass is needed for
	 * authentication in proxy, this parameter can be used for specifying the
	 * username.
	 */
	private String	proxyUsername					= null;

	/**
	 * If crawler should run behind a proxy and user/pass is needed for
	 * authentication in proxy, this parameter can be used for specifying the
	 * password.
	 */
	private String	proxyPassword					= null;

	public CrawlConfig()
	{
	}

	public int getConnectionTimeout()
	{
		return this.connectionTimeout;
	}

	public String getCrawlStorageFolder()
	{
		return this.crawlStorageFolder;
	}

	public int getMaxConnectionsPerHost()
	{
		return this.maxConnectionsPerHost;
	}

	public int getMaxDepthOfCrawling()
	{
		return this.maxDepthOfCrawling;
	}

	public int getMaxDownloadSize()
	{
		return this.maxDownloadSize;
	}

	public int getMaxOutgoingLinksToFollow()
	{
		return this.maxOutgoingLinksToFollow;
	}

	public int getMaxPagesToFetch()
	{
		return this.maxPagesToFetch;
	}

	public int getMaxTotalConnections()
	{
		return this.maxTotalConnections;
	}

	public int getPolitenessDelay()
	{
		return this.politenessDelay;
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

	public int getSocketTimeout()
	{
		return this.socketTimeout;
	}

	public String getUserAgentString()
	{
		return this.userAgentString;
	}

	public boolean isFollowRedirects()
	{
		return this.followRedirects;
	}

	public boolean isIncludeBinaryContentInCrawling()
	{
		return this.includeBinaryContentInCrawling;
	}

	public boolean isIncludeHttpsPages()
	{
		return this.includeHttpsPages;
	}

	public boolean isResumableCrawling()
	{
		return this.resumableCrawling;
	}

	/**
	 * Connection timeout in milliseconds
	 */
	public void setConnectionTimeout(int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * The folder which will be used by crawler for storing the intermediate
	 * crawl data. The content of this folder should not be modified manually.
	 */
	public void setCrawlStorageFolder(String crawlStorageFolder)
	{
		this.crawlStorageFolder = crawlStorageFolder;
	}

	/**
	 * Should we follow redirects?
	 */
	public void setFollowRedirects(boolean followRedirects)
	{
		this.followRedirects = followRedirects;
	}

	/**
	 * Should we fetch binary content such as images, audio, ...?
	 */
	public void setIncludeBinaryContentInCrawling(
			boolean includeBinaryContentInCrawling)
	{
		this.includeBinaryContentInCrawling = includeBinaryContentInCrawling;
	}

	/**
	 * Should we also crawl https pages?
	 */
	public void setIncludeHttpsPages(boolean includeHttpsPages)
	{
		this.includeHttpsPages = includeHttpsPages;
	}

	/**
	 * Maximum Connections per host
	 */
	public void setMaxConnectionsPerHost(int maxConnectionsPerHost)
	{
		this.maxConnectionsPerHost = maxConnectionsPerHost;
	}

	/**
	 * Maximum depth of crawling For unlimited depth this parameter should be
	 * set to -1
	 */
	public void setMaxDepthOfCrawling(int maxDepthOfCrawling)
	{
		this.maxDepthOfCrawling = maxDepthOfCrawling;
	}

	/**
	 * Max allowed size of a page. Pages larger than this size will not be
	 * fetched.
	 */
	public void setMaxDownloadSize(int maxDownloadSize)
	{
		this.maxDownloadSize = maxDownloadSize;
	}

	/**
	 * Max number of outgoing links which are processed from a page
	 */
	public void setMaxOutgoingLinksToFollow(int maxOutgoingLinksToFollow)
	{
		this.maxOutgoingLinksToFollow = maxOutgoingLinksToFollow;
	}

	/**
	 * Maximum number of pages to fetch For unlimited number of pages, this
	 * parameter should be set to -1
	 */
	public void setMaxPagesToFetch(int maxPagesToFetch)
	{
		this.maxPagesToFetch = maxPagesToFetch;
	}

	/**
	 * Maximum total connections
	 */
	public void setMaxTotalConnections(int maxTotalConnections)
	{
		this.maxTotalConnections = maxTotalConnections;
	}

	/**
	 * Politeness delay in milliseconds (delay between sending two requests to
	 * the same host).
	 * 
	 * @param politenessDelay
	 *            the delay in milliseconds.
	 */
	public void setPolitenessDelay(int politenessDelay)
	{
		this.politenessDelay = politenessDelay;
	}

	/**
	 * If crawler should run behind a proxy, this parameter can be used for
	 * specifying the proxy host.
	 */
	public void setProxyHost(String proxyHost)
	{
		this.proxyHost = proxyHost;
	}

	/**
	 * If crawler should run behind a proxy and user/pass is needed for
	 * authentication in proxy, this parameter can be used for specifying the
	 * password.
	 */
	public void setProxyPassword(String proxyPassword)
	{
		this.proxyPassword = proxyPassword;
	}

	/**
	 * If crawler should run behind a proxy, this parameter can be used for
	 * specifying the proxy port.
	 */
	public void setProxyPort(int proxyPort)
	{
		this.proxyPort = proxyPort;
	}

	/**
	 * If crawler should run behind a proxy and user/pass is needed for
	 * authentication in proxy, this parameter can be used for specifying the
	 * username.
	 */
	public void setProxyUsername(String proxyUsername)
	{
		this.proxyUsername = proxyUsername;
	}

	/**
	 * If this feature is enabled, you would be able to resume a previously
	 * stopped/crashed crawl. However, it makes crawling slightly slower
	 */
	public void setResumableCrawling(boolean resumableCrawling)
	{
		this.resumableCrawling = resumableCrawling;
	}

	/**
	 * Socket timeout in milliseconds
	 */
	public void setSocketTimeout(int socketTimeout)
	{
		this.socketTimeout = socketTimeout;
	}

	/**
	 * user-agent string that is used for representing your crawler to web
	 * servers. See http://en.wikipedia.org/wiki/User_agent for more details
	 */
	public void setUserAgentString(String userAgentString)
	{
		this.userAgentString = userAgentString;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Crawl storage folder: " + this.getCrawlStorageFolder()
				+ "\n");
		sb.append("Resumable crawling: " + this.isResumableCrawling() + "\n");
		sb.append("Max depth of crawl: " + this.getMaxDepthOfCrawling() + "\n");
		sb.append("Max pages to fetch: " + this.getMaxPagesToFetch() + "\n");
		sb.append("User agent string: " + this.getUserAgentString() + "\n");
		sb.append("Include https pages: " + this.isIncludeHttpsPages() + "\n");
		sb.append("Include binary content: "
				+ this.isIncludeBinaryContentInCrawling() + "\n");
		sb.append("Max connections per host: "
				+ this.getMaxConnectionsPerHost() + "\n");
		sb.append("Max total connections: " + this.getMaxTotalConnections()
				+ "\n");
		sb.append("Socket timeout: " + this.getSocketTimeout() + "\n");
		sb.append("Max total connections: " + this.getMaxTotalConnections()
				+ "\n");
		sb.append("Max outgoing links to follow: "
				+ this.getMaxOutgoingLinksToFollow() + "\n");
		sb.append("Max download size: " + this.getMaxDownloadSize() + "\n");
		sb.append("Should follow redirects?: " + this.isFollowRedirects()
				+ "\n");
		sb.append("Proxy host: " + this.getProxyHost() + "\n");
		sb.append("Proxy port: " + this.getProxyPort() + "\n");
		sb.append("Proxy username: " + this.getProxyUsername() + "\n");
		sb.append("Proxy password: " + this.getProxyPassword() + "\n");
		return sb.toString();
	}

	/**
	 * Validates the configs specified by this instance.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception
	{
		if (this.crawlStorageFolder == null)
		{
			throw new Exception(
					"Crawl storage folder is not set in the CrawlConfig.");
		}
		if (this.politenessDelay < 0)
		{
			throw new Exception("Invalid value for politeness delay: "
					+ this.politenessDelay);
		}
		if (this.maxDepthOfCrawling < -1)
		{
			throw new Exception(
					"Maximum crawl depth should be either a positive number or -1 for unlimited depth.");
		}
		if (this.maxDepthOfCrawling > Short.MAX_VALUE)
		{
			throw new Exception("Maximum value for crawl depth is "
					+ Short.MAX_VALUE);
		}

	}

}
