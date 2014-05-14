package guang.crawler.siteManager;

import guang.crawler.controller.CrawlerController;
import guang.crawler.controller.SiteInfo;

import java.util.List;

public class SiteManagerMain
{
	public static void main(String[] args) throws Exception
	{
		CrawlerController controller = new CrawlerController(
		        "ubuntu-3,ubuntu-2,ubuntu-6,ubuntu-7,ubuntu-8");
		List<SiteInfo> unHandledSites = controller.getUnHandledSites();
		SiteInfo siteToHandle = null;
		if ((unHandledSites == null) || (unHandledSites.size() == 0))
		{
			System.out.println("No site to crawl.");
			return;
		} else
		{
			
			for (SiteInfo info : unHandledSites)
			{
				boolean success = controller.handleSite(info);
				if (success)
				{
					siteToHandle = info;
					break;
				}
			}
			
		}
		if (siteToHandle == null)
		{
			System.out.println("No site to crawl.");
			return;
		}
		System.out.println("handle site " + siteToHandle.getSeedSite());
		SiteConfig config = SiteConfig.getConfig();
		config.setCrawlerHome(System.getProperty("crawler.home"));
		config.setSiteID(siteToHandle.getName());
		config.setSiteToHandle(siteToHandle);
		config.setCrawlerController(controller);
		SiteManager.init(config);
		SiteManager manager = SiteManager.getSiteManager();
		manager.start();
		
	}
}
