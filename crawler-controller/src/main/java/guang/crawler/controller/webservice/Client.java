package guang.crawler.controller.webservice;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Client {
	public static void main(String[] args) throws MalformedURLException {
		URL url = new URL("http://localhost:9876/ts?wsdl");
		QName name = new QName("http://guang.crawler.controller.webservice/",
				"SiteManagerService");
		Service service = Service.create(url, name);
		QName portName = new QName(
				"http://guang.crawler.controller.webservice/",
				"SiteManagerService");
		SiteManagerService proxy = service.getPort(portName,
				SiteManagerService.class);
		com.gzgb.epo.webservice.WebGatherNodeBean bean = new com.gzgb.epo.webservice.WebGatherNodeBean();
		boolean success = proxy.update(bean);
		System.out.println(success);
	}
}
