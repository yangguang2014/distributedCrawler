package guang.crawler.centerController.config;

import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.commons.service.WebGatherNodeBean;
import guang.crawler.connector.CenterConfigConnector;
import guang.crawler.util.PathHelper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

import com.alibaba.fastjson.JSON;

public class SiteInfo extends CenterConfigElement {
	private static final String KEY_SEED = "seedSite";
	private static final String KEY_HANDLED = "handled";
	private static final String KEY_SITE_MANAGER = "siteManager";
	private static final String KEY_SITE_CONFIG = "siteConfig";
	private static final String KEY_SITE_ENABLED = "site.enabled";
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

	public WebGatherNodeBean getWebGatherNodeInfo() {
		String configString = this.getProperty(SiteInfo.KEY_SITE_CONFIG);
		if (configString != null) {
			return JSON.parseObject(configString, WebGatherNodeBean.class);
		}
		return null;
	}

	public boolean isEnabled() {
		String enabled = this.getProperty(SiteInfo.KEY_SITE_ENABLED);
		if ("true".equalsIgnoreCase(enabled)) {
			return true;
		}
		return false;
	}

	public boolean isHandled() throws InterruptedException {
		String handled = this.getProperty(SiteInfo.KEY_HANDLED);
		if (handled == null) {
			return false;
		}
		return Boolean.parseBoolean(handled);
	}

	public void setEnabled(boolean enabled) throws InterruptedException,
			IOException, KeeperException {
		this.setProperty(SiteInfo.KEY_SITE_ENABLED, Boolean.toString(enabled),
				true);
	}

	public void setHandled(boolean handled, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteInfo.KEY_HANDLED, String.valueOf(handled),
				refreshNow);
	}

	/**
	 * 设置以逗号分隔的种子URL列表
	 * 
	 * @param seedSites
	 * @param refreshNow
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public void setSeedSites(String seedSites, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {

		this.setProperty(SiteInfo.KEY_SEED, seedSites, refreshNow);
	}

	public void setSiteManagerId(String siteManagerId, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteInfo.KEY_SITE_MANAGER, siteManagerId, refreshNow);
	}

	public void setWebGatherNodeInfo(WebGatherNodeBean info, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		if (info != null) {
			String configString = JSON.toJSONString(info);
			this.setProperty(SiteInfo.KEY_SITE_CONFIG, configString, refreshNow);
		}
	}

}
