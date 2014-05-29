package guang.crawler.centerController.controller;

import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

public class ControllerConfigInfo extends CenterConfigElement {

	private static final String MANAGER = "/manager";
	private static final String SERVICES = "/services";
	private ControllerManagerInfo controllerManagerInfo;

	private ControllerServicesInfo controllerServicesInfo;

	public ControllerConfigInfo(String path, CenterConfigConnector connector) {
		super(path, connector);
	}

	/**
	 * 竞争作为控制器
	 * 
	 * @param addr
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public boolean competeForController() throws InterruptedException,
			KeeperException {
		String realPath = this.connector.createNode(this.path
				+ ControllerConfigInfo.MANAGER, CreateMode.EPHEMERAL,
				"comment=manager of the controller".getBytes());
		if (realPath != null) {
			this.controllerManagerInfo = new ControllerManagerInfo(this,
					realPath, this.connector);
			String servicePath = this.path + ControllerConfigInfo.SERVICES;
			if (this.connector.isNodeExists(servicePath)) {
				this.connector.simpleDelete(servicePath, null);
			}
			this.connector.createNode(servicePath, CreateMode.EPHEMERAL,
					"comment=web services managed by controller".getBytes());
			this.controllerServicesInfo = new ControllerServicesInfo(
					servicePath, this.connector);
			return true;
		}
		return false;
	}

	/**
	 * 获取控制器的管理者信息
	 * 
	 * @return
	 */
	public ControllerManagerInfo getControllerManagerInfo() {
		if (this.controllerManagerInfo != null) {
			return this.controllerManagerInfo;
		} else {
			return new ControllerManagerInfo(this, this.path
					+ ControllerConfigInfo.MANAGER, this.connector);
		}
	}

	public ControllerServicesInfo getControllerServicesInfo() {
		if (this.controllerServicesInfo == null) {
			this.controllerServicesInfo = new ControllerServicesInfo(this.path
					+ ControllerConfigInfo.SERVICES, this.connector);
		}
		return this.controllerServicesInfo;
	}

}
