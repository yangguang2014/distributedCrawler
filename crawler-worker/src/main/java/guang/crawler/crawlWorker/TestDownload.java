package guang.crawler.crawlWorker;

import guang.crawler.commons.WebURL;
import guang.crawler.crawlWorker.fetcher.Downloader;

public class TestDownload {
	public static void main(String[] args) {
		// 既然是模拟，设置一下环境变量
		System.setProperty(
				"crawler.home",
				System.getProperty("user.home")
						+ "/work/workspace/distributedCrawler/target/distribute-crawler-1.0-SNAPSHOT-release");
		WorkerConfig.me().init();
		Downloader downloader = new Downloader();
		WebURL webUrl = new WebURL();
		webUrl.setURL("http://cn.msn.com/");
		downloader.processUrl(webUrl);
	}

}
