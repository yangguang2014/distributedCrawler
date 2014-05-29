package guang.crawler.centerController.config;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

public class SitesConfigInfo extends CenterConfigElement {

	public SitesConfigInfo(String path, CenterConfigConnector connector) {
		super(path, connector);
	}

	/**
	 * 获取所有已经处理的站点
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public List<SiteInfo> getAllHandledSites() throws InterruptedException,
			KeeperException {
		LinkedList<SiteInfo> result = new LinkedList<>();
		List<String> children = this.connector.getChildren(this.path
				+ CenterConfig.SITES_PATH);
		for (String child : children) {
			try {
				SiteInfo siteInfo = this.getSite(child);
				if ((siteInfo != null) && siteInfo.isHandled()) {
					result.add(siteInfo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * 获取所有尚未处理的站点
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public LinkedList<SiteInfo> getAllUnhandledSites()
			throws InterruptedException, KeeperException {
		LinkedList<SiteInfo> result = new LinkedList<>();
		List<String> children = this.connector.getChildren(this.path
				+ CenterConfig.SITES_PATH);
		for (String child : children) {
			try {
				SiteInfo siteInfo = this.getSite(child);
				if ((siteInfo != null) && !siteInfo.isHandled()) {
					result.add(siteInfo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	/**
	 * 根据站点ID获取采集点信息
	 * 
	 * @param siteId
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public SiteInfo getSite(String siteId) throws InterruptedException,
			IOException, KeeperException {
		String realPath = this.path + CenterConfig.SITES_PATH + "/" + siteId;
		boolean exist = this.connector.isNodeExists(realPath);
		if (!exist) {
			return null;
		}
		SiteInfo info = new SiteInfo(realPath, this.connector);
		info.load();
		return info;

	}

	/**
	 * 新增了一个站点
	 * 
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws IOException
	 */
	public SiteInfo registSite(String siteId) throws InterruptedException,
			IOException, KeeperException {
		String realPath = this.connector.createNode(this.path
				+ CenterConfig.SITES_PATH + "/" + siteId,
				CreateMode.PERSISTENT, "".getBytes());
		if (realPath != null) {
			SiteInfo siteInfo = new SiteInfo(realPath, this.connector);
			return siteInfo;
		}
		return null;
	}
}
