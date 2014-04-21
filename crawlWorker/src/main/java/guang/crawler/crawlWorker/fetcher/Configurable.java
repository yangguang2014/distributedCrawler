package guang.crawler.crawlWorker.fetcher;

/**
 * 可配置接口
 * 
 * @author yang
 */
public abstract class Configurable
{

	protected CrawlConfig	config;

	protected Configurable(CrawlConfig config)
	{
		this.config = config;
	}

	public CrawlConfig getConfig()
	{
		return this.config;
	}
}
