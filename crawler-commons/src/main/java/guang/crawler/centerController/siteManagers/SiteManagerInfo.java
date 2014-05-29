package guang.crawler.centerController.siteManagers;

import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;
import guang.crawler.core.GenericState;
import guang.crawler.util.PathHelper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class SiteManagerInfo extends CenterConfigElement {

	private final String siteManagerId;
	public static final String KEY_SITE_TOHANDLE = "site.toHandle";
	public static final String KEY_MANAGER_STATE = "manager.state";
	public static final String KEY_DISPATCHED = "manager.dispatched";
	public static final String KEY_MANAGER_ADDRESS = "manager.address";

	public SiteManagerInfo(String path, CenterConfigConnector connector) {
		super(path, connector);
		this.siteManagerId = PathHelper.getName(path);
	}

	public String getManagerAddress() {
		return this.getProperty(SiteManagerInfo.KEY_MANAGER_ADDRESS);
	}

	public GenericState getManagerState() {
		String managerState = this.getProperty(SiteManagerInfo.KEY_MANAGER_STATE);
		if (managerState == null) {
			return GenericState.registed;
		}
		return GenericState.valueOf(managerState);
	}

	public String getSiteManagerId() {
		return this.siteManagerId;
	}

	public String getSiteToHandle() {
		return this.getProperty(SiteManagerInfo.KEY_SITE_TOHANDLE);
	}

	public boolean isDispatched() {
		String dispatched = this.getProperty(SiteManagerInfo.KEY_DISPATCHED);
		if (dispatched == null) {
			return false;
		}
		return Boolean.parseBoolean(dispatched);
	}

	public void setDispatched(boolean dispatched, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteManagerInfo.KEY_DISPATCHED, String.valueOf(dispatched),
				refreshNow);
	}

	public void setManagerAddress(String managerAddress, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteManagerInfo.KEY_MANAGER_ADDRESS, managerAddress,
				refreshNow);
	}

	public void setManagerState(GenericState managerState, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteManagerInfo.KEY_MANAGER_STATE, managerState.toString(),
				refreshNow);
	}

	public void setSiteToHandle(String siteToHandle, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteManagerInfo.KEY_SITE_TOHANDLE, siteToHandle, refreshNow);
	}

}