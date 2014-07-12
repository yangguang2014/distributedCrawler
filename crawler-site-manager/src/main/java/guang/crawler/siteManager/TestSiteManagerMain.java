package guang.crawler.siteManager;

import java.io.File;

/**
 * 用来测试站点管理器
 * 
 * @author sun
 *
 */
public class TestSiteManagerMain {
	public static void main(final String[] args) throws Exception {
		File file = new File(
		        "../target/distribute-crawler-1.0-SNAPSHOT-release");
		System.setProperty("crawler.home", file.getCanonicalPath());
		System.out.println(System.getProperty("crawler.home"));
		SiteManager.me()
		           .init()
		           .start();
		
	}
}
