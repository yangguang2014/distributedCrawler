package guang.crawler.siteManager;

import java.io.File;

public class TestSiteManagerMain
{
	public static void main(String[] args) throws Exception
	{
		File file = new File(
		        "../target/distribute-crawler-1.0-SNAPSHOT-release");
		System.setProperty("crawler.home", file.getCanonicalPath());
		System.out.println(System.getProperty("crawler.home"));
		SiteManagerMain.main(args);
		
	}
}
