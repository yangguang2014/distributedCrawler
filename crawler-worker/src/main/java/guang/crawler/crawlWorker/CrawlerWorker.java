package guang.crawler.crawlWorker;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.workers.WorkerInfo;
import guang.crawler.commons.WebURL;
import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.crawlWorker.daemon.SiteManagerConnectorManager;
import guang.crawler.crawlWorker.fetcher.PageProcessor;
import guang.crawler.crawlWorker.plugin.ConfigLoadException;
import guang.crawler.crawlWorker.plugin.ExtractDataToSavePlugin;
import guang.crawler.crawlWorker.plugin.ExtractLinksToFollowPlugin;
import guang.crawler.crawlWorker.plugin.SaveExtractedDataPlugin;
import guang.crawler.crawlWorker.plugin.UploadExtractedLinksPlugin;

import java.io.IOException;

public class CrawlerWorker implements Runnable {
	private static CrawlerWorker	crawlerWorker;
	
	public static CrawlerWorker me() {
		if (CrawlerWorker.crawlerWorker == null) {
			CrawlerWorker.crawlerWorker = new CrawlerWorker();
		}
		return CrawlerWorker.crawlerWorker;
	}
	
	private SiteManagerConnectorManager	siteManagerConnectManager;
	private WorkerConfig	            workerConfig;
	private CenterConfig	            controller;
	private PageProcessor	            pageProcessor;
	private WebDataTableConnector	    webDataTableConnector;
	
	private CrawlerWorker() {
	}
	
	public CrawlerWorker init() throws IOException, InterruptedException,
	        ConfigLoadException {
		// 初始化各类配置信息
		this.workerConfig = WorkerConfig.me()
		                                .init();
		this.controller = CenterConfig.me()
		                              .init(this.workerConfig.getZookeeperQuorum());
		WorkerInfo workerInfo = this.controller.getWorkersInfo()
		                                       .getOnlineWorkers()
		                                       .registWorker();
		this.workerConfig.setCrawlerController(this.controller);
		this.workerConfig.setWorkerInfo(workerInfo);
		this.siteManagerConnectManager = SiteManagerConnectorManager.me()
		                                                            .init();
		this.webDataTableConnector = new WebDataTableConnector(
		        this.workerConfig.getZookeeperQuorum());
		try {
			this.webDataTableConnector.open();
		} catch (IOException e) {
			System.out.println("Can not open hbase connect");
		}
		// 添加页面处理器插件，当页面下载完成后依次调用这些插件
		this.pageProcessor = new PageProcessor();
		this.pageProcessor.addPlugin(new ExtractDataToSavePlugin());
		this.pageProcessor.addPlugin(new SaveExtractedDataPlugin(
		        this.webDataTableConnector));
		this.pageProcessor.addPlugin(new ExtractLinksToFollowPlugin());
		this.pageProcessor.addPlugin(new UploadExtractedLinksPlugin());
		return this;
	}
	
	@Override
	public void run() {
		this.siteManagerConnectManager.start();
		WebURL url = null;
		
		while (true) {
			try {
				url = this.siteManagerConnectManager.getURL();
			} catch (InterruptedException e) {
				break;
			}
			if (url != null) {
				this.pageProcessor.processUrl(url);
			}
		}
		
		try {
			this.siteManagerConnectManager.exit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.pageProcessor.shutdown();
		
	}
	
	public void start() {
		new Thread(this).start();
	}
}
