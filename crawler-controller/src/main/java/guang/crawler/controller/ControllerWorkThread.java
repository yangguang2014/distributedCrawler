package guang.crawler.controller;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.siteManagers.SiteManagerInfo;
import guang.crawler.centerConfig.siteManagers.SiteManagersConfigInfo;
import guang.crawler.centerConfig.sitesConfig.SiteInfo;
import guang.crawler.centerConfig.sitesConfig.SitesConfigInfo;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * 控制器工作线程,对系统中的采集点和站点管理器进行调度工作
 *
 * @author sun
 *
 */
public class ControllerWorkThread extends Thread implements Watcher {
	/**
	 * 当前类的单例
	 */
	private static ControllerWorkThread	controllerWorkThread;
	
	public static ControllerWorkThread me() {
		if (ControllerWorkThread.controllerWorkThread == null) {
			ControllerWorkThread.controllerWorkThread = new ControllerWorkThread();
			ControllerWorkThread.controllerWorkThread.setName("CRAWLER-CONTROLLER");
		}
		return ControllerWorkThread.controllerWorkThread;
	}
	
	/**
	 * 事件发生的时间
	 */
	private Date	eventTime	= new Date();
	
	private ControllerWorkThread() {
	}
	
	/**
	 * 强制触发一次调度事件
	 */
	public void forceReschedue() {
		synchronized (this.eventTime) {
			this.eventTime.setTime(System.currentTimeMillis());
			this.eventTime.notifyAll();
		}
	}
	
	/**
	 * 当监听的节点发生事件时进行事件的处理
	 */
	@Override
	public void process(final WatchedEvent event) {
		// 不管怎样，再次继续监听事件
		try {
			if (event.getPath()
			         .equals(CenterConfig.me()
			                             .getSiteManagersConfigInfo()
			                             .getOnlineSiteManagers()
			                             .getPath())) {
				if (event.getType() == EventType.NodeChildrenChanged) {
					CenterConfig.me()
					            .getSiteManagersConfigInfo()
					            .getOnlineSiteManagers()
					            .watchChildren(this);
				} else {
					CenterConfig.me()
					            .getSiteManagersConfigInfo()
					            .getOnlineSiteManagers()
					            .watchNode(this);
				}
			} else if (event.getPath()
			                .equals(CenterConfig.me()
			                                    .getSitesConfigInfo()
			                                    .getSitesInfo()
			                                    .getPath())) {
				if (event.getType() == EventType.NodeChildrenChanged) {
					CenterConfig.me()
					            .getSitesConfigInfo()
					            .getSitesInfo()
					            .watchChildren(this);
				}
			}
		} catch (Exception e) {
			return;
		}
		
		// 处理字节点的增减事件
		synchronized (this.eventTime) {
			this.eventTime.setTime(System.currentTimeMillis());
			this.eventTime.notifyAll();
		}
		
	}
	
	/**
	 * 主线程,对采集点和站点管理器进行调度.这部分是控制器的核心,应当着重看.
	 */
	@Override
	public void run() {
		CenterConfig centerConfig = CenterConfig.me();
		try {
			CenterConfig.me()
			            .getSiteManagersConfigInfo()
			            .getOnlineSiteManagers()
			            .watchNode(this);
			CenterConfig.me()
			            .getSiteManagersConfigInfo()
			            .getOnlineSiteManagers()
			            .watchChildren(this);
			CenterConfig.me()
			            .getSitesConfigInfo()
			            .getSitesInfo()
			            .watchChildren(this);
		} catch (Exception e) {
			return;
		}
		
		while (true) {
			try {
				Date now = new Date();
				SitesConfigInfo sitesConfigInfo = centerConfig.getSitesConfigInfo();
				SiteManagersConfigInfo siteManagersConfigInfo = centerConfig.getSiteManagersConfigInfo();
				// 首先检测一下当前分配的状态
				List<SiteInfo> handledSites = sitesConfigInfo.getSitesInfo()
				                                             .getAllHandledSites();
				for (SiteInfo siteInfo : handledSites) {
					String siteManagerId = siteInfo.getSiteManagerId();
					SiteManagerInfo siteManagerInfo = siteManagersConfigInfo.getOnlineSiteManagers()
					                                                        .getSiteManagerInfo(siteManagerId);
					if ((siteManagerInfo == null)
					        || !siteManagerInfo.isDispatched()
					        || !siteManagerInfo.getSiteToHandle()
					                           .equals(siteInfo.getSiteId())) {
						siteInfo.setHandled(false, false);
						siteInfo.setSiteManagerId("null", true);
					} else { // 如果一切分配的都对，那么检测一下该节点有没有被停止或者已经分配完成
						if (!siteInfo.isEnabled() || siteInfo.isFinished()) {
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
				List<SiteManagerInfo> undispatchedSiteManagers = siteManagersConfigInfo.getOnlineSiteManagers()
				                                                                       .getAllUndispatchedSiteManagers();
				List<SiteInfo> unhandledSites = sitesConfigInfo.getSitesInfo()
				                                               .getAllUnhandledSites();
				if ((undispatchedSiteManagers.size() > 0)
				        && (unhandledSites.size() > 0)) {
					int size = Math.min(undispatchedSiteManagers.size(),
					                    unhandledSites.size());
					Iterator<SiteManagerInfo> siteManagersIt = undispatchedSiteManagers.iterator();
					Iterator<SiteInfo> sitesIt = unhandledSites.iterator();
					for (int i = 0; i < size; i++) {
						SiteManagerInfo siteManager = siteManagersIt.next();
						SiteInfo siteInfo = sitesIt.next();
						siteManager.setDispatched(true, false);
						siteManager.setSiteToHandle(siteInfo.getSiteId(), false);
						siteInfo.setHandled(true, false);
						siteInfo.setSiteManagerId(siteManager.getSiteManagerId(),
						                          false);
						siteInfo.update();
						siteManager.update();
					}
				}
				
				// 做完工作之后，查看有没有更新的消息
				synchronized (this.eventTime) {
					if (now.after(this.eventTime)) {
						this.eventTime.wait();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
	}
}
