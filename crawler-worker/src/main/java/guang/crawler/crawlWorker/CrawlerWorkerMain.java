package guang.crawler.crawlWorker;

import guang.crawler.crawlWorker.pageProcessor.ConfigLoadException;

import java.io.IOException;

public class CrawlerWorkerMain {
	
	public static void main(final String[] args) throws IOException,
	        InterruptedException, ConfigLoadException {
		CrawlerWorker.me().init().start();
	}
	
}
