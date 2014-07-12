package guang.crawler.crawlWorker;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.workers.WorkerInfo;
import guang.crawler.commons.WebURL;
import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.crawlWorker.daemon.SiteManagerConnectorManager;
import guang.crawler.crawlWorker.pageProcessor.ConfigLoadException;
import guang.crawler.crawlWorker.pageProcessor.ExtractDataToSavePlugin;
import guang.crawler.crawlWorker.pageProcessor.ExtractLinksToFollowPlugin;
import guang.crawler.crawlWorker.pageProcessor.PageProcessor;
import guang.crawler.crawlWorker.pageProcessor.SaveExtractedDataPlugin;
import guang.crawler.crawlWorker.pageProcessor.UploadExtractedLinksPlugin;

import java.io.IOException;

/**
 * 爬虫工作者,用来启动一个爬虫.爬虫是一直存活的,不会死去.
 *
 * @author sun
 *
 */
public class CrawlerWorker implements Runnable {
	/**
	 * 爬虫工作者的单例
	 */
	private static CrawlerWorker	crawlerWorker;

	/**
	 * 获取爬虫工作者的单例
	 *
	 * @return
	 */
	public static CrawlerWorker me() {
		if (CrawlerWorker.crawlerWorker == null) {
			CrawlerWorker.crawlerWorker = new CrawlerWorker();
		}
		return CrawlerWorker.crawlerWorker;
	}
	
	/**
	 * 站点管理器连接的管理者,用来更新在线的站点管理器连接并获取有效的URL.
	 */
	private SiteManagerConnectorManager	siteManagerConnectManager;
	/**
	 * 爬虫工作者的本地配置信息
	 */
	private WorkerConfig	            workerConfig;
	/**
	 * 中央配置器
	 */
	private CenterConfig	            controller;
	/**
	 * 页面处理器,用来对下载的页面进行处理
	 */
	private PageProcessor	            pageProcessor;
	/**
	 * HBase中存放的网页数据的连接器
	 */
	private WebDataTableConnector	    webDataTableConnector;

	private CrawlerWorker() {
	}
	
	/**
	 * 初始化爬虫工作者
	 *
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ConfigLoadException
	 */
	public CrawlerWorker init() throws IOException, InterruptedException,
	        ConfigLoadException {
		// 加载本地配置信息
		this.workerConfig = WorkerConfig.me()
		                                .init();
		// 在中央配置器中注册一个爬虫工作者
		this.controller = CenterConfig.me()
		                              .init(this.workerConfig.getZookeeperQuorum());
		WorkerInfo workerInfo = this.controller.getWorkersInfo()
		                                       .getOnlineWorkers()
		                                       .registWorker();
		this.workerConfig.setCrawlerController(this.controller);
		this.workerConfig.setWorkerInfo(workerInfo);
		// 创建并初始化站点管理器连接的管理器
		this.siteManagerConnectManager = SiteManagerConnectorManager.me()
		                                                            .init();
		// 创建HBase的连接器并打开连接
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

	/**
	 * 爬虫工作者的工作主线程,不断的获取URL并进行页面的处理.
	 */
	@Override
	public void run() {
		// 开始查找在线的站点管理器
		this.siteManagerConnectManager.start();
		
		WebURL url = null;
		// 无限循环
		while (true) {
			// 获取一个可用的URL
			try {
				url = this.siteManagerConnectManager.getURL();
			} catch (InterruptedException e) {
				break;
			}
			// 获取URL之后处理该URL
			if (url != null) {
				this.pageProcessor.processUrl(url);
			}
		}// 循环体结束,说明有异常或者被中断
		 // 退出并关闭相关资源
		try {
			this.siteManagerConnectManager.exit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.pageProcessor.shutdown();
		
	}

	/**
	 * 在新的线程中启动爬虫工作者,当前的处理是单线程的,可以考虑改写成多线程的.
	 */
	public void start() {
		new Thread(this).start();
	}
}
