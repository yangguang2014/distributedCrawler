package guang.crawler.centerController.controller;

import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

import java.io.IOException;

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
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws IOException
	 */
	public ControllerManagerInfo getControllerManagerInfo()
			throws KeeperException, InterruptedException, IOException {
		if (this.controllerManagerInfo == null) {
			this.controllerManagerInfo = new ControllerManagerInfo(this,
					this.path + ControllerConfigInfo.MANAGER, this.connector);
			if (this.controllerManagerInfo.exists()) {
				this.controllerManagerInfo.load();
			}
		}
		return this.controllerManagerInfo;
	}

	public ControllerServicesInfo getControllerServicesInfo()
			throws KeeperException, InterruptedException, IOException {

		if (this.controllerServicesInfo == null) {
			this.controllerServicesInfo = new ControllerServicesInfo(this.path
					+ ControllerConfigInfo.SERVICES, this.connector);
			if (this.controllerServicesInfo.exists()) {
				this.controllerServicesInfo.load();
			}
		}
		return this.controllerServicesInfo;
	}

}
