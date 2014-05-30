package guang.crawler.controller.webservice;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Client {
	private static final String NAMESPACE = "http://guang.crawler.controller.webservice/";

	public static void main(String[] args) throws MalformedURLException {
		URL url = new URL("http://localhost:9876/ts?wsdl");
		QName name = new QName(NAMESPACE,
				"SiteManagerService");
		Service service = Service.create(url, name);
		QName portName = new QName(
				NAMESPACE,
				"SiteManagerService");
		SiteManagerService proxy = service.getPort(portName,
				SiteManagerService.class);
		guang.crawler.centerController.config.WebGatherNodeBean bean = new guang.crawler.centerController.config.WebGatherNodeBean();
		boolean success = proxy.update(bean);
		System.out.println(success);
	}
}
