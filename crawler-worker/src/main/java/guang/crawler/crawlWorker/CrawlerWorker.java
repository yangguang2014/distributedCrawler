package guang.crawler.crawlWorker;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.workers.WorkerInfo;
import guang.crawler.commons.WebURL;
import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.crawlWorker.fetcher.Downloader;
import guang.crawler.crawlWorker.plugins.ExtractOutGoingUrlsPlugin;
import guang.crawler.crawlWorker.plugins.SaveToHbasePlugin;
import guang.crawler.crawlWorker.util.SiteManagerConnectorManager;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

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
		
		WebURL url = null;
		try
		{
			while (true)
			{
				try
				{
					url = this.siteManagerConnectHelper.getURL();
					if (url != null)
					{
						this.downloader.processUrl(url);
					} else if (this.siteManagerConnectHelper
					        .getSiteManagerConnectorSize() == 0)
					{
						Thread.sleep(1000);
					}
				} catch (IOException ex)
				{
					continue;
				} catch (KeeperException ex)
				{
					continue;
				}
			}
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
