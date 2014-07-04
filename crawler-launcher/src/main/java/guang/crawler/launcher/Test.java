package guang.crawler.launcher;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.sitesConfig.SiteInfo;
import guang.crawler.commons.service.WebGatherNodeBean;
import guang.crawler.connector.WebDataTableConnector;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;

public class Test {
	
	public static void addSite() throws InterruptedException, IOException,
	        KeeperException {
		WebGatherNodeBean bean = new WebGatherNodeBean();
		bean.setId(1l);
		bean.setWgnEntryUrl("http://news.qq.com/a/20140702/002270.htm");
		bean.setWgnAllowRule("http://news\\.qq\\.com/.*,http://coral\\.qq\\.com/.*");
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

	public static void clearCenterConfig() throws IOException,
	        InterruptedException {
		CenterConfig.me()
		            .init("ubuntu-3,ubuntu-6,ubuntu-8")
		            .clear();
		CenterConfig.me()
		            .initPath();
	}

	public static void clearTables() throws IOException {
		WebDataTableConnector connector = new WebDataTableConnector(
		        "ubuntu-3,ubuntu-6,ubuntu-8");
		connector.open();
		try {
			List<String> tableNames = connector.getAllTables("site-.*");
			if (tableNames != null) {
				for (String tableName : tableNames) {
					connector.deleteTable(tableName);
				}
			}
			System.out.println("finished!");
		} finally {
			connector.close();
		}

	}
	
	public static void main(final String[] args) throws IOException,
	        InterruptedException, ClassNotFoundException, KeeperException {
		// 既然是模拟，设置一下环境变量
		System.setProperty("crawler.home",
		                   System.getProperty("user.home")
		                           + "/work/workspace/distributedCrawler/target/distribute-crawler-1.0-SNAPSHOT-release");
		// 仅为测试使用，重新配置中央配置器
		// Test.clearCenterConfig();
		// Test.addSite();
		CrawlerLauncherMain.main(args);

		// Test.clearTables();
	}
}
