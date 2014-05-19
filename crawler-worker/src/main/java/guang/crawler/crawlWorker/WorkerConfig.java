package guang.crawler.crawlWorker;

import guang.crawler.controller.CrawlerController;
import guang.crawler.util.PropertiesHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
	 * 从配置文件加载的属性信息
	 */
	private Properties	      configProperties;
	/**
	 * crawler的home
	 */
	private String	          crawlerHome;

	private CrawlerController	crawlerController;

	/**
	 * zookeeper的连接字符串
	 */
	private String	          zookeeperURL;
	
	private WorkerConfig()
	{
	}
	
	public CrawlerController getCrawlerController()
	{
		return this.crawlerController;
	}

	public String getCrawlerHome()
	{
		return this.crawlerHome;
	}
	
	public String getZookeeperURL()
	{
		return this.zookeeperURL;
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
		File file = new File(this.crawlerHome
				+ "/conf/crawler-worker/crawler-worker.config");
		if (file.exists())
		{
			try
			{
				this.configProperties.load(new FileInputStream(file));
			} catch (IOException e)
			{
				System.err.println("Error load site-manager config file");
			}
		}

		this.zookeeperURL = PropertiesHelper.readString(this.configProperties,
				"crawler.site-manager.zookeeper.url", null);

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
