package guang.crawler.launcher;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.sitesConfig.SiteInfo;
import guang.crawler.commons.service.WebGatherNodeBean;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class Test {
	private static void addSite() throws InterruptedException, IOException,
	        KeeperException {
		WebGatherNodeBean bean = new WebGatherNodeBean();
		bean.setId(1l);
		bean.setWgnEntryUrl("http://news.qq.com/");

		SiteInfo siteInfo = CenterConfig.me()
		                                .init("ubuntu-3,ubuntu-6,ubuntu-8")
		                                .getSitesConfigInfo()
		                                .getSitesInfo()
		                                .registSite("testSite001");
		siteInfo.setWebGatherNodeInfo(bean, false)
		        .setEnabled(true)
		        .setFinished(false, false)
		        .setHandled(false, false)
		        .update();
	}
	
	private static void clearCenterConfig() throws IOException,
	        InterruptedException {
		CenterConfig.me()
		            .init("ubuntu-3,ubuntu-6,ubuntu-8")
		            .clear();
		CenterConfig.me()
		            .initPath();
	}

	public static void main(final String[] args) throws IOException,
	        InterruptedException, ClassNotFoundException, KeeperException {
		// 既然是模拟，设置一下环境变量
		System.setProperty("crawler.home",
		                   System.getProperty("user.home")
		                           + "/work/workspace/distributedCrawler/target/distribute-crawler-1.0-SNAPSHOT-release");
		// 仅为测试使用，重新配置中央配置器
		Test.clearCenterConfig();
		Test.addSite();
		CrawlerLauncherMain.main(args);
	}
}
