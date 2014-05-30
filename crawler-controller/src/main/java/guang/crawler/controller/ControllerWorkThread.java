package guang.crawler.controller;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.config.SiteInfo;
import guang.crawler.centerController.config.SitesConfigInfo;
import guang.crawler.centerController.siteManagers.SiteManagerInfo;
import guang.crawler.centerController.siteManagers.SiteManagersConfigInfo;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * 该
 * 
 * @author sun
 * 
 */
public class ControllerWorkThread extends Thread implements Watcher {
	private static ControllerWorkThread controllerWorkThread;

	public static ControllerWorkThread me() {
		if (ControllerWorkThread.controllerWorkThread == null) {
			ControllerWorkThread.controllerWorkThread = new ControllerWorkThread();
			ControllerWorkThread.controllerWorkThread
					.setName("CRAWLER-CONTROLLER");
		}
		return ControllerWorkThread.controllerWorkThread;
	}

	/**
	 * 一开始是假设有一次事件的
	 */
	private Date waitForEvent = new Date();

	private ControllerWorkThread() {
	}

	/**
	 * 强制触发一次调度事件
	 */
	public void forceReschedue() {
		synchronized (this.waitForEvent) {
			this.waitForEvent.setTime(System.currentTimeMillis());
			this.waitForEvent.notifyAll();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		// 不管怎样，再次继续监听事件
		try {
			CenterConfig.me().getSiteManagersConfigInfo()
					.getOnlineSiteManagers().watch(this);
		} catch (KeeperException | InterruptedException | IOException e) {
			e.printStackTrace();
			return;
		}
		if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
			// 处理字节点的增减事件
			synchronized (this.waitForEvent) {
				this.waitForEvent.setTime(System.currentTimeMillis());
				this.waitForEvent.notifyAll();
			}
		}
	}

	@Override
	public void run() {
		CenterConfig centerConfig = CenterConfig.me();
		try {
			centerConfig.getSiteManagersConfigInfo().getOnlineSiteManagers()
					.watch(this);
		} catch (KeeperException | InterruptedException | IOException e1) {
			return;
		}
		while (true) {
			try {
				Date now = new Date();
				SitesConfigInfo sitesConfigInfo = centerConfig
						.getSitesConfigInfo();
				SiteManagersConfigInfo siteManagersConfigInfo = centerConfig
						.getSiteManagersConfigInfo();
				// 首先检测一下当前分配的状态
				List<SiteInfo> handledSites = sitesConfigInfo
						.getAllHandledSites();
				for (SiteInfo siteInfo : handledSites) {
					String siteManagerId = siteInfo.getSiteManagerId();
					SiteManagerInfo siteManagerInfo = siteManagersConfigInfo
							.getOnlineSiteManagers().getSiteManagerInfo(
									siteManagerId);
					if ((siteManagerInfo == null)
							|| !siteManagerInfo.isDispatched()
							|| !siteManagerInfo.getSiteToHandle().equals(
									siteInfo.getSiteId())) {
						siteInfo.setHandled(false, false);
						siteInfo.setSiteManagerId("null", true);
					} else { // 如果一切分配的都对，那么检测一下该节点有没有被停止
						if (!siteInfo.isEnabled()) {
							siteManagerInfo.setDispatched(false, false);
							siteManagerInfo.setSiteToHandle("", false);
							siteManagerInfo.update();
							siteInfo.setHandled(false, false);
							siteInfo.setSiteManagerId("", false);
							siteInfo.update();
						}
					}
				}
				// 然后做统一的分配
				List<SiteManagerInfo> undispatchedSiteManagers = siteManagersConfigInfo
						.getOnlineSiteManagers()
						.getAllUndispatchedSiteManagers();
				List<SiteInfo> unhandledSites = sitesConfigInfo
						.getAllUnhandledSites();
				if ((undispatchedSiteManagers.size() > 0)
						&& (unhandledSites.size() > 0)) {
					int size = Math.max(undispatchedSiteManagers.size(),
							unhandledSites.size());
					Iterator<SiteManagerInfo> siteManagersIt = undispatchedSiteManagers
							.iterator();
					Iterator<SiteInfo> sitesIt = unhandledSites.iterator();
					for (int i = 0; i < size; i++) {
						SiteManagerInfo siteManager = siteManagersIt.next();
						SiteInfo siteInfo = sitesIt.next();
						siteManager.setDispatched(true, false);
						siteManager
								.setSiteToHandle(siteInfo.getSiteId(), false);
						siteInfo.setHandled(true, false);
						siteInfo.setSiteManagerId(
								siteManager.getSiteManagerId(), false);
						siteInfo.update();
						siteManager.update();
					}
				}

				// 做完工作之后，查看有没有更新的消息
				synchronized (this.waitForEvent) {
					if (now.after(this.waitForEvent)) {
						this.waitForEvent.wait();
					}
				}
			} catch (InterruptedException | IOException | KeeperException e) {
				e.printStackTrace();
				return;
			}
		}

	}
}
