package guang.crawler.crawlWorker;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.workers.WorkerInfo;
import guang.crawler.commons.WebURL;
import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.crawlWorker.daemon.SiteManagerConnectorManager;
import guang.crawler.crawlWorker.fetcher.Downloader;
import guang.crawler.crawlWorker.plugins.ExtractOutGoingUrlsPlugin;
import guang.crawler.crawlWorker.plugins.SaveToHbasePlugin;

import java.io.IOException;

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
	
	private SiteManagerConnectorManager	siteManagerConnectManager;
	private WorkerConfig	            workerConfig;
	private CenterConfig	            controller;
	private Downloader	                downloader;
	private WebDataTableConnector	    webDataTableConnector;
	
	private CrawlerWorker()
	{
	}
	
	public CrawlerWorker init() throws IOException, InterruptedException
	{
		this.workerConfig = WorkerConfig.me().init();
		this.controller = CenterConfig.me().init(
		        this.workerConfig.getZookeeperQuorum());
		WorkerInfo workerInfo = this.controller.getWorkersInfo()
		        .getOnlineWorkers().registWorker();
		this.workerConfig.setCrawlerController(this.controller);
		this.workerConfig.setWorkerInfo(workerInfo);
		this.siteManagerConnectManager = SiteManagerConnectorManager.me()
		        .init();
		this.downloader = new Downloader();
		ExtractOutGoingUrlsPlugin extractOutGoingUrlsPlugin = new ExtractOutGoingUrlsPlugin();
		
		this.webDataTableConnector = new WebDataTableConnector(
		        this.workerConfig.getZookeeperQuorum());
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
		this.siteManagerConnectManager.start();
		WebURL url = null;
		
		while (true)
		{
			try
			{
				url = this.siteManagerConnectManager.getURL();
			} catch (InterruptedException e)
			{
				break;
			}
			if (url != null)
			{
				this.downloader.processUrl(url);
			}
		}
		
		try
		{
			this.siteManagerConnectManager.exit();
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
