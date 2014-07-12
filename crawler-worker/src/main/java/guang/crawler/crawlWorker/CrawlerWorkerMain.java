package guang.crawler.crawlWorker;

import guang.crawler.crawlWorker.pageProcessor.ConfigLoadException;

import java.io.IOException;

/**
 * 爬虫工作者的主类
 * 
 * @author sun
 *
 */
public class CrawlerWorkerMain {

	public static void main(final String[] args) throws IOException,
	        InterruptedException, ConfigLoadException {
		CrawlerWorker.me()
		             .init()
		             .start();
	}

}
