package com.gzgb.epo.webservice;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.centerController.controller.ControllerServicesInfo;

import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.zookeeper.KeeperException;

public class Main {
	private static final String MANAGER_SERVICE = "managerService";
	private static final String NAMESPACE = "http://guang.crawler.controller.webservice/";

	public static void main(String[] args) throws IOException,
			InterruptedException, KeeperException {

		// 这里配置的是zookeeper的主机名，需要根据实际的部署环境确定
		CenterConfig config = CenterConfig.me().init(
				"ubuntu-3,ubuntu-6,ubuntu-8");
		ControllerServicesInfo serviceInfo = config.getControllerInfo()
				.getControllerServicesInfo();
		if (serviceInfo == null) {
			System.out.println("web service should be started!");
			return;
		}
		String serviceAddr = serviceInfo
				.getServiceAddress(Main.MANAGER_SERVICE);
		if (serviceAddr == null) {
			System.out.println("can not find the manager service.");
			return;
		}
		URL url = new URL(serviceAddr + "?wsdl");
		QName name = new QName(Main.NAMESPACE, "SiteManagerService");
		Service service = Service.create(url, name);
		QName portName = new QName(Main.NAMESPACE, "SiteManagerService");
		SiteManagerService proxy = service.getPort(portName,
				SiteManagerService.class);
		WebGatherNodeInfo info = new WebGatherNodeInfo();
		info.setId(new Long(200));
		info.setWgnEntryUrl("http://www.tudou.com/");
		boolean success = proxy.add(info);
		System.out.println(success);
	}
}
