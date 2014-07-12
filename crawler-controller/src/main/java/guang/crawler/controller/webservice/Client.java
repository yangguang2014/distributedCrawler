package guang.crawler.controller.webservice;

import guang.crawler.commons.service.SiteManagerService;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * 该类是客户端连接控制器发布的web服务的例子.其他应用连接服务都应该按照这种方式进行.
 * 
 * @author sun
 *
 */
public class Client {
	private static final String	NAMESPACE	= "http://guang.crawler.controller.webservice/";
	
	public static void main(final String[] args) throws MalformedURLException {
		URL url = new URL("http://localhost:9876/ts?wsdl");
		QName name = new QName(Client.NAMESPACE, "SiteManagerService");
		Service service = Service.create(url, name);
		QName portName = new QName(Client.NAMESPACE, "SiteManagerService");
		SiteManagerService proxy = service.getPort(portName,
		                                           SiteManagerService.class);
		guang.crawler.commons.service.WebGatherNodeBean bean = new guang.crawler.commons.service.WebGatherNodeBean();
		boolean success = proxy.update(bean);
		System.out.println(success);
	}
}
