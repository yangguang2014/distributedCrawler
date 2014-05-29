package guang.crawler.centerController.config;

import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;
import guang.crawler.util.PathHelper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class SiteInfo extends CenterConfigElement {
	private static final String KEY_SEED = "seedSite";
	private static final String KEY_HANDLED = "handled";
	private static final String KEY_SITE_MANAGER = "siteManager";
	/**
	 * 该站点注册时获取的一个注册ID。
	 */
	private final String siteId;

	public SiteInfo(String path, CenterConfigConnector connector) {
		super(path, connector);
		this.siteId = PathHelper.getName(path);
	}

	public String[] getSeedSites() throws InterruptedException {
		String seeds = this.getProperty(SiteInfo.KEY_SEED);
		return seeds.split(",");
	}

	public String getSiteId() {
		return this.siteId;
	}

	public String getSiteManagerId() throws InterruptedException {
		return this.getProperty(SiteInfo.KEY_SITE_MANAGER);
	}

	public boolean isHandled() throws InterruptedException {
		String handled = this.getProperty(SiteInfo.KEY_HANDLED);
		if (handled == null) {
			return false;
		}
		return Boolean.parseBoolean(handled);
	}

	public void setHandled(boolean handled, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteInfo.KEY_HANDLED, String.valueOf(handled), refreshNow);
	}

	public void setSeedSites(String[] seedSites, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < (seedSites.length - 1); i++) {
			result.append(seedSites);
			result.append(",");
		}
		result.append(seedSites[seedSites.length - 1]);
		this.setProperty(SiteInfo.KEY_SEED, result.toString(), refreshNow);
	}

	public void setSiteManagerId(String siteManagerId, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteInfo.KEY_SITE_MANAGER, siteManagerId, refreshNow);
	}

}
