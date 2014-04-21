package guang.crawler.siteManager;

import guang.crawler.controller.CrawlerController;

public class Test
{
	public static void main(String[] args)
	{
		String prefix = CrawlerController.ROOT_PATH
		        + CrawlerController.CONFIG_PATH
		        + CrawlerController.UN_HANDLED_SITES;
		String s = prefix + "/test";
		System.out.println(s.substring(prefix.length()));
	}
	
}
