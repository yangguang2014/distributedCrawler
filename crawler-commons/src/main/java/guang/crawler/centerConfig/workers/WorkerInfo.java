package guang.crawler.centerConfig.workers;

import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.ZookeeperConnector;
import guang.crawler.util.PathHelper;

/**
 * 某个具体爬虫的信息
 * 
 * @author sun
 *
 */
public class WorkerInfo extends CenterConfigElement {
	
	/**
	 * 当前爬虫工作者的ID
	 */
	private final String	workerID;
	
	public WorkerInfo(final String path, final ZookeeperConnector connector) {
		super(path, connector);
		this.workerID = PathHelper.getName(path);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 获取当前爬虫的ID.
	 * 
	 * @return
	 */
	public String getWorkerID() {
		return this.workerID;
	}
	
}
