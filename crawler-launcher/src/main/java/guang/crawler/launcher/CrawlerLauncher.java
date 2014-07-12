package guang.crawler.launcher;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.controller.CrawlerController;
import guang.crawler.crawlWorker.CrawlerWorker;
import guang.crawler.siteManager.SiteManager;

import java.io.IOException;

/**
 * 爬虫的启动器,用来启动整个爬虫系统
 *
 * @author sun
 *
 */
public class CrawlerLauncher {
	/**
	 * 站点管理器角色常量
	 */
	private static final String	   ROLE_SITE	   = "site";
	/**
	 * 爬虫工作者角色常量
	 */
	private static final String	   ROLE_WORKER	   = "worker";
	/**
	 * 控制器角色常量
	 */
	private static final String	   ROLE_CONTROLLER	= "controller";

	private static CrawlerLauncher	crawlerLauncher;

	public static CrawlerLauncher me() {
		if (CrawlerLauncher.crawlerLauncher == null) {
			CrawlerLauncher.crawlerLauncher = new CrawlerLauncher();
		}
		return CrawlerLauncher.crawlerLauncher;
	}

	private CrawlerLauncher() {
	}

	/**
	 * 初始化爬虫启动器
	 *
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public CrawlerLauncher init() throws IOException, InterruptedException {
		LauncherConfig launcherConfig = LauncherConfig.me()
		                                              .init();
		CenterConfig.me()
		            .init(launcherConfig.getZookeeperQuorum());
		return this;

	}

	/**
	 * 根据配置的角色加载各个角色
	 *
	 * @throws InterruptedException
	 */
	public void launch() throws InterruptedException {
		String[] roles = LauncherConfig.me()
		                               .getRoles();
		for (String role : roles) {
			if (CrawlerLauncher.ROLE_WORKER.equalsIgnoreCase(role)) {
				this.launchWorker();
			} else if (CrawlerLauncher.ROLE_SITE.equalsIgnoreCase(role)) {
				this.launchSiteManager();
			} else if (CrawlerLauncher.ROLE_CONTROLLER.equalsIgnoreCase(role)) {
				this.launchController();
			}
		}
	}

	/**
	 * 在一个单独的线程中启动控制器
	 * 
	 * @throws InterruptedException
	 */
	private void launchController() throws InterruptedException {

		new Thread() {
			@Override
			public void run() {
				try {
					CrawlerController.me()
					                 .init()
					                 .start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
	}

	/**
	 * 在一个单独的线程中启动站点管理器
	 */
	private void launchSiteManager() {
		// 这里启动站点管理器
		new Thread() {
			@Override
			public void run() {
				try {
					SiteManager.me()
					           .init()
					           .start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 在一个单独的线程中启动爬虫工作者
	 */
	private void launchWorker() {

		// 这里启动爬虫工作者
		new Thread() {
			@Override
			public void run() {
				try {
					CrawlerWorker.me()
					             .init()
					             .start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
	}

}
