package guang.crawler.statistics;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.controller.ControllerManagerInfo;
import guang.crawler.centerController.controller.ControllerServicesInfo;
import guang.crawler.centerController.siteManagers.SiteManagerInfo;
import guang.crawler.centerController.sitesConfig.SiteInfo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

public class StatisticsGetter implements TimerTask
{
	
	private static StatisticsGetter	statisticsGetter;
	
	public static StatisticsGetter me()
	{
		if (StatisticsGetter.statisticsGetter == null)
		{
			StatisticsGetter.statisticsGetter = new StatisticsGetter();
		}
		return StatisticsGetter.statisticsGetter;
	}
	
	private StatisticsGetter()
	{
	}
	
	public void init() throws IOException, InterruptedException
	{
		StatisticsConfig.me().init();
		CenterConfig.me().init(StatisticsConfig.me().getZookeeperQuorum());
	}
	
	@Override
	public void run(Timeout arg0) throws Exception
	{
		System.out.println("======= info =======");
		this.showControllerManagerInfo();
		this.showSiteInfo();
		this.showSiteManagersInfo();
	}
	
	private void showControllerManagerInfo() throws KeeperException,
	        InterruptedException, IOException
	{
		CenterConfig centerConfig = CenterConfig.me();
		ControllerManagerInfo controllerManagerInfo = centerConfig
		        .getControllerInfo().getControllerManagerInfo();
		if (!controllerManagerInfo.exists())
		{
			System.out.println("[controller manager] not exist.");
		} else
		{
			System.out.println("[controller manager] "
			        + controllerManagerInfo.getControllerManagerAddress());
		}
		ControllerServicesInfo controllerServicesInfo = centerConfig
		        .getControllerInfo().getControllerServicesInfo();
		if (!controllerServicesInfo.exists())
		{
			System.out.println("[controller services] not exist.");
		} else
		{
			System.out.println("[controller services]"
			        + controllerServicesInfo.getServicesInfo());
		}
	}
	
	private void showSiteInfo() throws InterruptedException, KeeperException,
	        IOException
	{
		CenterConfig centerConfig = CenterConfig.me();
		LinkedList<SiteInfo> sites = centerConfig.getSitesConfigInfo()
		        .getSitesInfo().getAllSites();
		if ((sites == null) || (sites.size() == 0))
		{
			System.out.println("[sites config] no site registered.");
			return;
		}
		System.out.println("[sites config]");
		for (SiteInfo site : sites)
		{
			System.out.println("--[site" + site.getSiteId() + "] "
			        + site.getWebGatherNodeInfo().getWgnEntryUrl()
			        + " handled:" + site.isHandled() + " enabled:"
			        + site.isEnabled());
		}
	}
	
	private void showSiteManagersInfo() throws InterruptedException,
	        KeeperException, IOException
	{
		CenterConfig centerConfig = CenterConfig.me();
		List<SiteManagerInfo> siteManagers = centerConfig
		        .getSiteManagersConfigInfo().getOnlineSiteManagers()
		        .getAllSiteManagers();
		if ((siteManagers == null) || (siteManagers.size() == 0))
		{
			System.out.println("[site manager] no site manager registered.");
			return;
		}
		System.out.println("[site manager]");
		for (SiteManagerInfo siteManager : siteManagers)
		{
			System.out.println("--[manager"
			        + siteManager.getSiteManagerId()
			        + "] dispatched:"
			        + siteManager.isDispatched()
			        + (siteManager.isDispatched() ? " siteToHandle:"
			                + siteManager.getSiteToHandle() : ""));
		}
	}
	
}
