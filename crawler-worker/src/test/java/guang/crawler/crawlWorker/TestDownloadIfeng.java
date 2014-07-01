package guang.crawler.crawlWorker;

import guang.crawler.commons.WebURL;
import guang.crawler.crawlWorker.pageProcessor.PageProcessor;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDownloadIfeng {
	
	@BeforeClass
	public static void setup() {
		// 既然是模拟，设置一下环境变量
		System.setProperty(
		        "crawler.home",
		        System.getProperty("user.home")
		                + "/work/workspace/distributedCrawler/target/distribute-crawler-1.0-SNAPSHOT-release");
		WorkerConfig.me().init();
	}
	
	private PageProcessor	downloader;
	
	@Before
	public void createDownloader() {
		this.downloader = new PageProcessor();
	}
	
	@After
	public void shutdownDownloader() {
		this.downloader.shutdown();
	}
	
	@Test
	public void testGetUrls() {
		this.downloader.addPlugin(new EchoCommentURLPlugin());
		WebURL webUrl = WebURL.newWebURL().setURL(
		        "http://news.ifeng.com/opinion/special/huanghaibopiaochang/");
		this.downloader.processUrl(webUrl);
		webUrl.setURL("http://comment.ifeng.com/viewspecial.php?doc_name=%E7%BD%91%E5%8F%8B%E2%80%9C%E7%82%B9%E8%B5%9E%E2%80%9D%E9%BB%84%E6%B5%B7%E6%B3%A2%E5%AB%96%E5%A8%BC%20%E4%BC%A4%E4%BA%86%E8%B0%81%E7%9A%84%E9%81%93%E5%BE%B7%EF%BC%9F&doc_url=http%3A%2F%2Fnews.ifeng.com%2Fopinion%2Fspecial%2Fhuanghaibopiaochang%2F&p=1&skey=849d8e");
		this.downloader.processUrl(webUrl);
	}
	
}
