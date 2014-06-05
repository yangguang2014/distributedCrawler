package guang.crawler.crawlWorker;

import java.io.IOException;

public class CrawlerWorkerMain {

	public static void main(String[] args) throws IOException,
			InterruptedException {
		CrawlerWorker.me().init().start();
	}

}
