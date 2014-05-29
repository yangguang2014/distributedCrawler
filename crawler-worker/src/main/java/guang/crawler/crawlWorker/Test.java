package guang.crawler.crawlWorker;

import guang.crawler.core.WebURL;
import guang.crawler.crawlWorker.fetcher.Downloader;

public class Test {
	public static void main(String[] args) {
		System.setProperty(
				"crawler.home",
				"/home/sun/work/workspace/distribute-crawler/target/distribute-crawler-1.0-SNAPSHOT-release");
		WorkerConfig.me().init();
		Downloader downloader = new Downloader();
		WebURL url = new WebURL();
		url.setURL("http://www.baidu.com");
		downloader.processUrl(url);
	}
}
