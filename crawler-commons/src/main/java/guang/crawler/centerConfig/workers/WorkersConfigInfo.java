package guang.crawler.centerConfig.workers;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.ZookeeperConnector;

/**
 * 爬虫工作者的配置入口类
 *
 * @author sun
 *
 */
public class WorkersConfigInfo extends CenterConfigElement {

	/**
	 * 缓存的当前所有在线的爬虫工作者.
	 */
	private OnlineWorkers	onlineWorkers;

	public WorkersConfigInfo(final String path,
	        final ZookeeperConnector connector) {
		super(path, connector);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取当前所有在线的爬虫工作者
	 * 
	 * @return
	 */
	public OnlineWorkers getOnlineWorkers() {
		if (this.onlineWorkers == null) {
			this.onlineWorkers = new OnlineWorkers(this.path
			        + CenterConfig.ONLINE_WORKER_PATH, this.connector);
		}
		return this.onlineWorkers;
	}

}
