package guang.crawler.crawlWorker;

import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.controller.CrawlerController;
import guang.crawler.core.WebURL;
import guang.crawler.crawlWorker.fetcher.Downloader;
import guang.crawler.crawlWorker.plugins.ExtractOutGoingUrlsPlugin;
import guang.crawler.crawlWorker.plugins.SaveToHbasePlugin;
import guang.crawler.crawlWorker.util.SiteManagerConnectorManager;

import java.io.IOException;
import java.util.LinkedList;

public class CrawlerWorker implements Runnable
{
	private static CrawlerWorker	crawlerWorker;

	public static CrawlerWorker me()
	{
		if (CrawlerWorker.crawlerWorker == null)
		{
			CrawlerWorker.crawlerWorker = new CrawlerWorker();
		}
		return CrawlerWorker.crawlerWorker;
	}

	private SiteManagerConnectorManager	siteManagerConnectHelper;
	private WorkerConfig	            workerConfig;
	private CrawlerController	        controller;
	private Downloader	                downloader;
	private WebDataTableConnector	    webDataTableConnector;
	
	private CrawlerWorker()
	{
	}
	
	public CrawlerWorker init() throws IOException
	{
		this.workerConfig = WorkerConfig.me().init();
		this.controller = new CrawlerController(
		        this.workerConfig.getZookeeperQuorum());
		try
		{
			this.siteManagerConnectHelper = new SiteManagerConnectorManager(
			        this.controller);
		} catch (IOException e)
		{
			System.out.println("Can not connect to site manager");
			throw e;
		}
		this.downloader = new Downloader();
		ExtractOutGoingUrlsPlugin extractOutGoingUrlsPlugin = new ExtractOutGoingUrlsPlugin();
		extractOutGoingUrlsPlugin
		        .setSiteManagerConnector(this.siteManagerConnectHelper);
		
		this.webDataTableConnector = new WebDataTableConnector(
		        this.workerConfig.getZookeeperQuorum(),
		        this.workerConfig.getZookeeperClientPort());
		try
		{
			this.webDataTableConnector.open();
		} catch (IOException e)
		{
			System.out.println("Can not open hbase connect");
		}
		SaveToHbasePlugin saveToHbasePlugin = new SaveToHbasePlugin(
		        this.webDataTableConnector);
		this.downloader.addPlugin(extractOutGoingUrlsPlugin);
		this.downloader.addPlugin(saveToHbasePlugin);
		return this;
	}
	
	@Override
	public void run()
	{
		for (int i = 0; i < 100000; i++)
		{
			LinkedList<WebURL> urls = null;
			try
			{
				
				urls = this.siteManagerConnectHelper.getURLs(1);
			} catch (IOException ex)
			{
				ex.printStackTrace();
				break;
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (urls != null)
			{
				for (WebURL url : urls)
				{
					this.downloader.processUrl(url);
				}
			} else
			{
				try
				{
					Thread.sleep(10000);
				} catch (InterruptedException e)
				{
					// Nothing to do.
				}
			}
		}
		try
		{
			this.siteManagerConnectHelper.exit();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		this.downloader.shutdown();
		
	}
	
	public void start()
	{
		new Thread(this).start();
	}
}
