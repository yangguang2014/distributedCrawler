package guang.crawler.centerConfig.workers;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

/**
 * 该类代表当前所有在线的爬虫工作者.
 * 
 * @author sun
 *
 */
public class OnlineWorkers extends CenterConfigElement {

	public OnlineWorkers(final String path, final ZookeeperConnector connector) {
		super(path, connector);
	}

	/**
	 * 根据crawler worker的ID获取其信息
	 *
	 * @param crawlerWorkerID
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public WorkerInfo getWorkerInfo(final String crawlerWorkerID)
	        throws InterruptedException, IOException, KeeperException {
		String realPath = this.path + CenterConfig.ONLINE_WORKER_PATH + "/"
		        + crawlerWorkerID;
		if (this.connector.isNodeExists(realPath)) {
			WorkerInfo workerInfo = new WorkerInfo(realPath, this.connector);
			workerInfo.load();
			return workerInfo;
		} else {
			return null;
		}

	}

	/**
	 * 新增了一个crawler worker节点
	 *
	 * @throws InterruptedException
	 */
	public WorkerInfo registWorker() throws InterruptedException {
		String realPath = this.connector.createNode(this.path
		                                                    + "/crawler-worker",
		                                            CreateMode.EPHEMERAL_SEQUENTIAL,
		                                            "A crawler worker registered.".getBytes());
		if (realPath != null) {
			return new WorkerInfo(realPath, this.connector);
		}
		return null;
	}

}
