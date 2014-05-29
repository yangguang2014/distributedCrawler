package com.gzgb.epo.webservice;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Main {
	public static void main(String[] args) throws MalformedURLException {
		URL url = new URL("http://localhost:9876/ts?wsdl");
		QName name = new QName("http://guang/crawler/controller",
				"SiteManagerService");
		Service service = Service.create(name);
		// SiteManagerService proxy = service.getPort(SiteManagerService.class);
		// WebGatherNodeBean bean = new WebGatherNodeBean();
		// boolean success = proxy.update(bean);
		// System.out.println(success);
	}
}
