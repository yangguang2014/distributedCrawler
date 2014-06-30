package guang.crawler.crawlWorker;

import guang.crawler.crawlWorker.plugin.ConfigLoadException;

import java.io.IOException;

public class CrawlerWorkerMain {
	
	public static void main(final String[] args) throws IOException,
	        InterruptedException, ConfigLoadException {
		CrawlerWorker.me().init().start();
	}
	
}
