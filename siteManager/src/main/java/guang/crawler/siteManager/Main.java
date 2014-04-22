package guang.crawler.siteManager;

import guang.crawler.controller.CrawlerController;
import guang.crawler.controller.SiteInfo;
import guang.crawler.core.PortDefine;

import java.util.List;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		CrawlerController controller = new CrawlerController(
		        "ubuntu-3,ubuntu-2,ubuntu-6,ubuntu-7,ubuntu-8");
		List<SiteInfo> unHandledSites = controller.getUnHandledSites();
		if ((unHandledSites == null) || (unHandledSites.size() == 0))
		{
			System.out.println("No site to crawl.");
			return;
		} else
		{
			
		}
		SiteConfig config = SiteConfig.getConfig();
		config.setSiteID("siteManager_0");
		config.setBaseDir("/home/yang/tmp/craw");
		config.setListenPort(PortDefine.PORT_SITE_MANAGER);
		config.setSeedURL(new String[] { "http://www.tudou.com/" });
		SiteManager.init(config);
		SiteManager manager = SiteManager.getSiteManager();
		manager.start();
		
	}
}
