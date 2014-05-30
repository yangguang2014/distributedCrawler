package guang.crawler.controller.webservice;

import javax.xml.ws.Endpoint;

public class Main {
	public static void main(String[] args) {
		Endpoint.publish("http://localhost:9876/ts",
				new SiteManagerServiceImp());

	}
}
