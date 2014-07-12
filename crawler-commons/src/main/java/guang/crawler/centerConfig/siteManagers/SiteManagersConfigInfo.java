package guang.crawler.centerConfig.siteManagers;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.ZookeeperConnector;

/**
 * 站点管理器配置信息的入口类,可以用来对站点管理器进行相关配置
 *
 * @author sun
 *
 */
public class SiteManagersConfigInfo extends CenterConfigElement {
	/**
	 * 在线的站点管理器,为了防止重复连接该节点,在这里缓存之.
	 */
	private OnlineSiteManagers	onlineSiteManagers;
	
	/**
	 * 创建站点管理器配置信息对象
	 *
	 * @param path
	 *            当前节点的路径
	 * @param connector
	 *            Zookeeper连接器
	 */
	public SiteManagersConfigInfo(final String path,
	        final ZookeeperConnector connector) {
		super(path, connector);
	}
	
	/**
	 * 获取所有在线的站点管理器.
	 * 
	 * @return
	 */
	public OnlineSiteManagers getOnlineSiteManagers() {
		if (this.onlineSiteManagers == null) {
			this.onlineSiteManagers = new OnlineSiteManagers(this.path
			        + CenterConfig.ONLINE_SITEMANAGERS_PATH, this.connector);
		}
		return this.onlineSiteManagers;
	}
	
}
