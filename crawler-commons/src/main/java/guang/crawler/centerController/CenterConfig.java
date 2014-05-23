package guang.crawler.centerController;

import guang.crawler.connector.CenterConfigConnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;

public class CenterConfig {
	private static CenterConfig centerController;
	public static final String CONFIG_PATH = "/config";
	public static final String ROOT_PATH = "/crawler";
	public static final String SITES_PATH = "/sites";
	public static final String WORKERS_PATH = "/workers";
	public static final String CONTROLLER_PATH = "/controller";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		CenterConfig controller = CenterConfig.me().init(
				"ubuntu-3,ubuntu-2,ubuntu-6,ubuntu-7,ubuntu-8");
		controller.clear();
		controller.reset();
		controller.addSite("quanbenNovel",
				new String[] { "http://www.quanben.com/" });
		controller.addSite("tudou", new String[] { "http://www.tudou.com/" });
		controller.shutdown();
	}

	public static CenterConfig me() {
		if (CenterConfig.centerController == null) {
			CenterConfig.centerController = new CenterConfig();
		}
		return CenterConfig.centerController;
	}

	private CenterConfigConnector connector;

	private CenterConfig() {
	}

	public boolean addSite(String name, String[] seedSite) {
		try {
			String path = CenterConfig.ROOT_PATH + CenterConfig.CONFIG_PATH
					+ CenterConfig.SITES_PATH + "/" + name;
			this.connector.createNode(path, CreateMode.PERSISTENT,
					"".getBytes());
			SiteInfo siteInfo = new SiteInfo(path, name, this.connector);
			siteInfo.setSeedSites(seedSite);
			return siteInfo.update(null);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean clear() {
		try {
			boolean success = this.connector.recursiveDelete(
					CenterConfig.ROOT_PATH, null);
			return success;
		} catch (InterruptedException e) {
			return false;
		}
	}

	public ControllerInfo getControllerInfo() {
		return new ControllerInfo(CenterConfig.ROOT_PATH
				+ CenterConfig.CONTROLLER_PATH, this.connector);
	}

	public List<SiteInfo> getHandledSites() throws InterruptedException {
		String path = CenterConfig.ROOT_PATH + CenterConfig.CONFIG_PATH
				+ CenterConfig.SITES_PATH;
		List<String> childPaths = this.connector.getChildren(path);
		if (childPaths != null) {
			ArrayList<SiteInfo> result = new ArrayList<>(childPaths.size());
			for (String cp : childPaths) {
				SiteInfo info = new SiteInfo(path + "/" + cp, cp,
						this.connector);
				if (info.isHandled()) {
					result.add(info);
				}

			}
			return result;
		}
		return null;
	}

	public List<SiteInfo> getUnHandledSites() throws InterruptedException {
		String path = CenterConfig.ROOT_PATH + CenterConfig.CONFIG_PATH
				+ CenterConfig.SITES_PATH;
		List<String> childPaths = this.connector.getChildren(path);
		if (childPaths != null) {
			ArrayList<SiteInfo> result = new ArrayList<>(childPaths.size());
			for (String cp : childPaths) {
				SiteInfo info = new SiteInfo(path + "/" + cp, cp,
						this.connector);
				if (!info.isHandled()) {
					result.add(info);
				}

			}
			return result;
		}
		return null;
	}

	/**
	 * 将当前站点注册给某个站点管理器管理。
	 * 
	 * @param site
	 * @return
	 * @throws InterruptedException
	 */
	public boolean handleSite(SiteInfo site) throws InterruptedException {
		if (site.getPath().startsWith(
				CenterConfig.ROOT_PATH + CenterConfig.CONFIG_PATH
						+ CenterConfig.SITES_PATH)) {
			boolean locked = site.lock();
			if (locked) {
				try {
					boolean handled = site.isHandled();
					if (!handled) {
						site.setHandled(true);
						return true;
					}
				} finally {
					site.unlock();
				}
			}
		}
		return false;
	}

	public CenterConfig init(String connectString) throws IOException {
		this.connector = new CenterConfigConnector(connectString);
		return this;
	}

	/**
	 * 初始化整个控制树
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean reset() throws InterruptedException, IOException {

		String path = this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH,
				CreateMode.PERSISTENT, "crawler config node".getBytes());
		if (path != null) {
			path = this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
					+ CenterConfig.CONFIG_PATH, CreateMode.PERSISTENT,
					"config the crawler".getBytes());
		}
		if (path != null) {
			path = this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
					+ CenterConfig.CONFIG_PATH + CenterConfig.SITES_PATH,
					CreateMode.PERSISTENT,
					"config all site manager or that want to be site manager"
							.getBytes());
		}
		if (path != null) {
			path = this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
					+ CenterConfig.CONTROLLER_PATH, CreateMode.PERSISTENT,
					"config the controller".getBytes());
		}
		if (path != null) {
			path = this.connector.checkAndCreateNode(CenterConfig.ROOT_PATH
					+ CenterConfig.WORKERS_PATH, CreateMode.PERSISTENT,
					"config the workers".getBytes());
		}
		return path != null;
	}

	public void shutdown() throws InterruptedException {
		this.connector.shutdown();
	}
}
