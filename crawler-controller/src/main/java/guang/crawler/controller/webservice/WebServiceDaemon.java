package guang.crawler.controller.webservice;

import guang.crawler.centerController.CenterConfig;
import guang.crawler.controller.ControllerConfig;
import guang.crawler.util.NetworkHelper;

import java.io.IOException;

import javax.xml.ws.Endpoint;

import org.apache.zookeeper.KeeperException;

public class WebServiceDaemon
{
	private static final String	    MANAGER_SERVICE	= "managerService";
	private static WebServiceDaemon	webServiceDaemon;
	
	public static WebServiceDaemon me()
	{
		if (WebServiceDaemon.webServiceDaemon == null)
		{
			WebServiceDaemon.webServiceDaemon = new WebServiceDaemon();
		}
		return WebServiceDaemon.webServiceDaemon;
	}
	
	private ControllerConfig	controllerConfig;
	private Endpoint	     managerService;
	
	private WebServiceDaemon()
	{
		this.controllerConfig = ControllerConfig.me();
	}
	
	public void start() throws IOException, InterruptedException,
	        KeeperException
	{
		// 先获取需要的端口号
		int port = this.controllerConfig.getWebserviceSuggestPort();
		// 启动服务端
		String address = "http://" + NetworkHelper.getIPAddress() + ":" + port
		        + "/crawler/controller/managerService";
		this.managerService = Endpoint.publish(address,
		        new SiteManagerServiceImp());
		// 注册该服务
		CenterConfig.me().getControllerInfo().getControllerServicesInfo()
		        .registService(WebServiceDaemon.MANAGER_SERVICE, address);
	}
	
	public void stop()
	{
		try
		{
			CenterConfig.me().getControllerInfo().getControllerServicesInfo()
			        .unRegistService(WebServiceDaemon.MANAGER_SERVICE);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (this.managerService != null)
		{
			this.managerService.stop();
		}
	}
}
