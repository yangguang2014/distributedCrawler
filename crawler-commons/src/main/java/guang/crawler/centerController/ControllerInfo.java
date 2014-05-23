package guang.crawler.centerController;

import guang.crawler.connector.CenterConfigConnector;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

public class ControllerInfo extends CenterConfigElement {

	public static class ManagerInfo extends CenterConfigElement {

		private static final String KEY_CONTROLLER_MANAGER_ADDR = "controller.manager.address";

		public ManagerInfo(String path, CenterConfigConnector connector) {
			super(path, connector);
		}

		public String getControllerManagerAddress() throws InterruptedException {
			return this.get(ManagerInfo.KEY_CONTROLLER_MANAGER_ADDR, true);
		}

		public void setControllerManagerAddress(String address)
				throws InterruptedException {
			this.put(ManagerInfo.KEY_CONTROLLER_MANAGER_ADDR, address, true);
		}
	}

	private ManagerInfo controllerManagerInfo;

	public ControllerInfo(String path, CenterConfigConnector connector) {
		super(path, connector);
	}

	/**
	 * 竞争作为控制器
	 * 
	 * @param addr
	 * @return
	 * @throws InterruptedException
	 */
	public boolean competeForController() throws InterruptedException {
		String realPath = this.connector.createNode(this.path + "/manager",
				CreateMode.EPHEMERAL, "manager of the controller".getBytes());
		if (realPath != null) {
			this.controllerManagerInfo = new ManagerInfo(realPath,
					this.connector);
			return true;
		}
		return false;
	}

	/**
	 * 获取控制器的管理者信息
	 * 
	 * @return
	 */
	public ManagerInfo getControllerManagerInfo() {
		return this.controllerManagerInfo;
	}

	public void watchManager(Watcher watcher) throws KeeperException,
			InterruptedException {
		this.connector.watch(this.path + "/manager", watcher);
	}

}
