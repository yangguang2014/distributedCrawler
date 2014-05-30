package guang.crawler.controller.webservice;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.config.SiteInfo;
import guang.crawler.centerController.config.WebGatherNodeBean;
import guang.crawler.centerController.siteManagers.SiteManagerInfo;
import guang.crawler.controller.ControllerWorkThread;

import java.io.IOException;

import javax.jws.WebService;

import org.apache.zookeeper.KeeperException;

@WebService(
		targetNamespace = "http://guang.crawler.controller.webservice/",
		portName = "SiteManagerService",
		serviceName = "SiteManagerService",
		endpointInterface = "guang.crawler.controller.webservice.SiteManagerService")
public class SiteManagerServiceImp implements SiteManagerService {

	@Override
	public boolean add(WebGatherNodeBean site) {
		try {
			SiteInfo siteInfo = CenterConfig.me().getSitesConfigInfo()
					.registSite(site.getId().toString());
			String urls = site.getWgnEntryUrl();
			siteInfo.setSeedSites(urls, false);
			siteInfo.setWebGatherNodeInfo(site, false);
			siteInfo.update();
			ControllerWorkThread.me().forceReschedue();
			return true;
		} catch (InterruptedException | IOException | KeeperException e) {
			return false;
		}
	}

	@Override
	public boolean delete(Long siteID) {
		try {
			SiteInfo siteInfo = CenterConfig.me().getSitesConfigInfo()
					.getSite(siteID.toString());
			if (siteInfo == null) {
				return true;
			}
			// 先将其设置为不可用的状态
			siteInfo.setEnabled(false);
			// 然后解除其关联关系
			if (siteInfo.isHandled()) {
				SiteManagerInfo siteManagerInfo = CenterConfig.me()
						.getSiteManagersConfigInfo().getOnlineSiteManagers()
						.getSiteManagerInfo(siteInfo.getSiteManagerId());
				if (siteManagerInfo.getSiteToHandle().equals(
						siteInfo.getSiteId())) {
					siteManagerInfo.setDispatched(false, false);
					siteManagerInfo.setSiteToHandle("", false);
					siteManagerInfo.update();
				}
			}
			// 最后，删除该节点
			siteInfo.delete(null);
			// 重新调度
			ControllerWorkThread.me().forceReschedue();
			return true;
		} catch (InterruptedException | IOException | KeeperException e) {
			return false;
		}
	}

	@Override
	public boolean disable(Long siteID) {
		try {
			SiteInfo siteInfo = CenterConfig.me().getSitesConfigInfo()
					.getSite(siteID.toString());
			if (siteInfo == null) {
				return false;
			}
			siteInfo.setEnabled(false);
			ControllerWorkThread.me().forceReschedue();
			return true;
		} catch (InterruptedException | IOException | KeeperException e) {
			return false;
		}
	}

	@Override
	public boolean enable(Long siteID) {
		try {
			SiteInfo siteInfo = CenterConfig.me().getSitesConfigInfo()
					.getSite(siteID.toString());
			if (siteInfo == null) {
				return false;
			}
			siteInfo.setEnabled(true);
			ControllerWorkThread.me().forceReschedue();
			return true;
		} catch (InterruptedException | IOException | KeeperException e) {
			return false;
		}
	}

	@Override
	public SiteStatus status(Long siteID) {
		try {
			SiteInfo siteInfo = CenterConfig.me().getSitesConfigInfo()
					.getSite(siteID.toString());
			if (siteInfo == null) {
				return SiteStatus.notexist;
			} else if (siteInfo.isEnabled() && !siteInfo.isHandled()) {
				return SiteStatus.enabled;
			} else if (siteInfo.isEnabled() && siteInfo.isHandled()) {
				return SiteStatus.running;
			} else {
				return SiteStatus.disabled;
			}
		} catch (InterruptedException | IOException | KeeperException e) {
			return SiteStatus.error;
		}
	}

	@Override
	public boolean update(WebGatherNodeBean site) {
		try {
			SiteInfo siteInfo = CenterConfig.me().getSitesConfigInfo()
					.getSite(site.getId().toString());
			if (siteInfo == null) {
				return false;
			}
			siteInfo.setSeedSites(site.getWgnEntryUrl(), false);
			siteInfo.setWebGatherNodeInfo(site, false);
			siteInfo.update();
			return true;
		} catch (InterruptedException | IOException | KeeperException e) {
			return false;
		}
	}

}
