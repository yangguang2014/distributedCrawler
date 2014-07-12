package guang.crawler.centerConfig.sitesConfig;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;

/**
 * 当前类是采集点配置的入口类
 *
 * @author sun
 *
 */
public class SitesConfigInfo extends CenterConfigElement {

	/**
	 * 为了防止重复查询,缓存了当前所有的采集点的信息
	 */
	private SitesInfo	sitesInfo;

	/**
	 * 构造函数
	 *
	 * @param path
	 *            采集点配置入口节点的路径
	 * @param connector
	 *            Zookeeper的连接器
	 */
	public SitesConfigInfo(final String path, final ZookeeperConnector connector) {
		super(path, connector);
	}

	/**
	 * 获取当前所有采集点的信息
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public SitesInfo getSitesInfo() throws InterruptedException, IOException {
		if (this.sitesInfo == null) {
			this.sitesInfo = new SitesInfo(this.path + CenterConfig.SITES_PATH,
			        this.connector);
			this.sitesInfo.load();
		}
		return this.sitesInfo;
	}

}
