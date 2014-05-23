package guang.crawler.launcher;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.ControllerInfo;
import guang.crawler.crawlWorker.CrawlerWorker;
import guang.crawler.siteManager.SiteManager;

import java.io.IOException;

public class CrawlerLauncher {
	private static final String ROLE_SITE = "site";

	private static final String ROLE_WORKER = "worker";

	private static final String ROLE_CONTROLLER = "controller";

	private static CrawlerLauncher crawlerLauncher;

	public static void main(String[] args) {
	}

	public static CrawlerLauncher me() {
		if (CrawlerLauncher.crawlerLauncher == null) {
			CrawlerLauncher.crawlerLauncher = new CrawlerLauncher();
		}
		return CrawlerLauncher.crawlerLauncher;
	}

	private CrawlerLauncher() {
	}

	public void init() throws IOException {
		LauncherConfig.me().init();
		CenterConfig.me().init(LauncherConfig.me().getZookeeperQuorum());
	}

	public void launch() throws InterruptedException {
		String[] roles = LauncherConfig.me().getRoles();
		for (String role : roles) {
			switch (role) {
			case ROLE_WORKER:
				this.launchWorker();
				break;
			case ROLE_SITE:
				this.launchSiteManager();
				break;
			case ROLE_CONTROLLER:
				this.launchController();
				break;
			}
		}
	}

	private void launchController() throws InterruptedException {
		CenterConfig centerConfig = CenterConfig.me();
		ControllerInfo controllerInfo = centerConfig.getControllerInfo();
		boolean success = controllerInfo.competeForController();
		if (success) {
			// TODO start controller;
		} else {

		}
	}

	private void launchSiteManager() {
		// 这里启动站点管理器
		new Thread() {
			@Override
			public void run() {
				try {
					SiteManager.me().init().start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void launchWorker() {
		// 这里启动爬虫工作者
		new Thread() {
			@Override
			public void run() {
				try {
					CrawlerWorker.me().init().start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

}
