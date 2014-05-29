package guang.crawler.centerController.controller;

import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

import org.apache.zookeeper.CreateMode;

public class ControllerConfigInfo extends CenterConfigElement {

	private static final String MANAGER = "/manager";
	private ControllerManagerInfo controllerManagerInfo;

	public ControllerConfigInfo(String path, CenterConfigConnector connector) {
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
		String realPath = this.connector.createNode(this.path
				+ ControllerConfigInfo.MANAGER, CreateMode.EPHEMERAL,
				"manager of the controller".getBytes());
		if (realPath != null) {
			this.controllerManagerInfo = new ControllerManagerInfo(this,
					realPath, this.connector);
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

}
