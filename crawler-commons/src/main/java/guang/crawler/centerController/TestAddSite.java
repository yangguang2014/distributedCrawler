package guang.crawler.centerController;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class TestAddSite
{
	
	public static void main(String[] args) throws InterruptedException,
	        IOException, KeeperException
	{
		// SiteInfo siteInfo = CenterConfig.me()
		// .init("ubuntu-3,ubuntu-6,ubuntu-8").getSitesConfigInfo()
		// .getSitesInfo().registSite("site1");
		// WebGatherNodeBean configBean = new WebGatherNodeBean();
		// configBean.setWgnEntryUrl("http://www.quanben.com/");
		// siteInfo.setWebGatherNodeInfo(configBean, false);
		// siteInfo.setHandled(false, false);
		// siteInfo.update();
		CenterConfig.me().init("ubuntu-3,ubuntu-6,ubuntu-8")
		        .getSitesConfigInfo().getSitesInfo().getSite("506")
		        .setFinished(false, true);
	}
}
