package guang.crawler.centerController;

import guang.crawler.centerController.config.SiteInfo;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class TestAddSite {

	public static void main(String[] args) throws InterruptedException,
			IOException, KeeperException {
		SiteInfo siteInfo = CenterConfig.me()
				.init("ubuntu-3,ubuntu-6,ubuntu-8").getSitesConfigInfo()
				.registSite("site1");
		siteInfo.setSeedSites(new String[] { "http://www.quanben.com/" }, false);
		siteInfo.setHandled(false, false);
		siteInfo.update();
	}
}
