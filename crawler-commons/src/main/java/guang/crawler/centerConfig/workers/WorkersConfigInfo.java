package guang.crawler.centerConfig.workers;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

public class WorkersConfigInfo extends CenterConfigElement {

	private OnlineWorkers onlineWorkers;

	public WorkersConfigInfo(String path, CenterConfigConnector connector) {
		super(path, connector);
		// TODO Auto-generated constructor stub
	}

	public OnlineWorkers getOnlineWorkers() {
		if (this.onlineWorkers == null) {
			this.onlineWorkers = new OnlineWorkers(this.path
					+ CenterConfig.ONLINE_WORKER_PATH, this.connector);
		}
		return this.onlineWorkers;
	}

}
