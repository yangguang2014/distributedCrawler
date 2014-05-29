package guang.crawler.siteManager;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.config.SitesConfigInfo;
import guang.crawler.centerController.siteManagers.SiteManagerInfo;

import java.io.IOException;
import java.util.Date;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class SiteManagerWatcher implements Watcher, Runnable {

	private Date eventTime = new Date();

	public SiteManagerWatcher() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(WatchedEvent event) {
		// 首先，立即注册一个事件监听
		SiteManagerInfo siteManagerInfo = SiteConfig.me().getSiteManagerInfo();
		try {
			siteManagerInfo.watch(this);
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
			// 如果节点信息更改了
			try {
				siteManagerInfo.load();
				synchronized (this.eventTime) {
					this.eventTime.setTime(System.currentTimeMillis());
					this.eventTime.notifyAll();
				}
			} catch (InterruptedException | IOException e) {
				return;
			}

		}
	}

	@Override
	public void run() {
		// 首先，立即注册一个事件监听
		SiteConfig siteConfig = SiteConfig.me();
		SiteManagerInfo siteManagerInfo = siteConfig.getSiteManagerInfo();
		SitesConfigInfo sitesConfigInfo;
		try {
			sitesConfigInfo = CenterConfig.me().getSitesConfigInfo();
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		try {
			siteManagerInfo.watch(this);
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (true) {
			// 作第一次的处理
			Date now = new Date();
			try {
				siteManagerInfo.load();
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			boolean dispatched = siteManagerInfo.isDispatched();
			String siteToHandle = siteManagerInfo.getSiteToHandle();
			if (dispatched && siteConfig.isDispatched()) {
				if (!siteToHandle.equals(siteConfig.getSiteToHandle()
						.getSiteId())) {
					// 这样导致该站点被重新配置了
					// 首先关闭当前已经运行的站点管理器
					SiteManager.me().shutdown();
					// 然后使用新的配置
					try {
						siteConfig.setSiteToHandle(sitesConfigInfo
								.getSite(siteManagerInfo.getSiteToHandle()));
					} catch (InterruptedException | IOException
							| KeeperException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
					}
					// 使用新的配置启动系统
					try {
						SiteManager.me().startSiteManager();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
			} else if (!dispatched && siteConfig.isDispatched()) {
				// 如果是这种情况，那么说明该站点被终止了
				SiteManager.me().shutdown();
				// 更新配置信息
				siteConfig.setDispatched(false);
				siteConfig.setSiteToHandle(null);
			} else if (dispatched && !siteConfig.isDispatched()) {
				// 如果是这种情况，那么说明该站点被分配了一个新的站点
				siteConfig.setDispatched(true);
				try {
					siteConfig.setSiteToHandle(sitesConfigInfo
							.getSite(siteManagerInfo.getSiteToHandle()));
				} catch (InterruptedException | IOException | KeeperException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
				// 启动相关部分
				try {
					SiteManager.me().startSiteManager();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
			synchronized (this.eventTime) {
				if (now.after(this.eventTime)) {
					try {
						this.eventTime.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
			}
		}

	}

}
