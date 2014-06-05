package guang.crawler.centerController;

import guang.crawler.centerController.sitesConfig.SiteInfo;
import guang.crawler.commons.service.WebGatherNodeBean;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class TestAddSite
{
	
	public static void main(String[] args) throws InterruptedException,
	        IOException, KeeperException
	{
		SiteInfo siteInfo = CenterConfig.me()
		        .init("ubuntu-3,ubuntu-6,ubuntu-8").getSitesConfigInfo()
		        .registSite("site1");
		WebGatherNodeBean configBean = new WebGatherNodeBean();
		configBean.setWgnEntryUrl("http://www.quanben.com/");
		siteInfo.setWebGatherNodeInfo(configBean, false);
		siteInfo.setHandled(false, false);
		siteInfo.update();
	}
}
