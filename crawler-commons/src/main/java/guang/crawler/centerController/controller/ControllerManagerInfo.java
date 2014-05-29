package guang.crawler.centerController.controller;

import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class ControllerManagerInfo extends CenterConfigElement {

	private final ControllerConfigInfo controllerConfigInfo;

	private static final String KEY_CONTROLLER_MANAGER_ADDR = "controller.manager.address";

	public ControllerManagerInfo(ControllerConfigInfo controllerConfigInfo,
			String path, CenterConfigConnector connector) {
		super(path, connector);
		this.controllerConfigInfo = controllerConfigInfo;
	}

	public ControllerConfigInfo getControllerConfigInfo() {
		return this.controllerConfigInfo;
	}

	public String getControllerManagerAddress() throws InterruptedException {
		return this.getProperty(ControllerManagerInfo.KEY_CONTROLLER_MANAGER_ADDR);
	}

	public void setControllerManagerAddress(String address, boolean refreshNow)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(ControllerManagerInfo.KEY_CONTROLLER_MANAGER_ADDR, address,
				refreshNow);
	}
}