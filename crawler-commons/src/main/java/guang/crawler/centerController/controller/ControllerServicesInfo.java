package guang.crawler.centerController.controller;

import guang.crawler.centerController.CenterConfigElement;
import guang.crawler.connector.CenterConfigConnector;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class ControllerServicesInfo extends CenterConfigElement {

	public ControllerServicesInfo(String path, CenterConfigConnector connector) {
		super(path, connector);
	}

	public String getServiceAddress(String serviceName) {
		return this.getProperty(serviceName);
	}

	public String getServicesInfo() {
		return this.getProperties().toString();
	}

	public boolean registService(String serviceName, String address)
			throws InterruptedException, IOException, KeeperException {
		this.setProperty(serviceName, address, true);
		return true;
	}

	public boolean unRegistService(String serviceName)
			throws InterruptedException, IOException, KeeperException {
		this.deleteProperty(serviceName, true);
		return true;
	}

}
