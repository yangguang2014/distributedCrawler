package guang.crawler.centerConfig.controller;

import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

/**
 * 代表控制器节点下的服务信息节点的类.每个控制器都可能需要发布一些服务,这些服务就存储在当前节点中.
 *
 * @author sun
 *
 */
public class ControllerServicesInfo extends CenterConfigElement {

	/**
	 * 创建服务信息节点对象
	 *
	 * @param path
	 *            当前节点的路径
	 * @param connector
	 *            Zookeeper连接器
	 */
	public ControllerServicesInfo(final String path,
	        final ZookeeperConnector connector) {
		super(path, connector);
	}

	/**
	 * 获取某个服务的地址
	 *
	 * @param serviceName
	 *            需要获取的服务的名称
	 * @return
	 */
	public String getServiceAddress(final String serviceName) {
		return this.getProperty(serviceName);
	}

	/**
	 * 获取所有的服务器信息
	 *
	 * @return
	 */
	public String getServicesInfo() {
		return this.getProperties()
		           .toString();
	}

	/**
	 * 注册一个服务
	 *
	 * @param serviceName
	 *            注册的服务的名称
	 * @param address
	 *            注册的服务的地址
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public boolean registService(final String serviceName, final String address)
	        throws InterruptedException, IOException, KeeperException {
		this.setProperty(serviceName, address, true);
		return true;
	}

	/**
	 * 取消对某个服务的注册
	 *
	 * @param serviceName
	 *            需要注销的服务的名称
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public boolean unRegistService(final String serviceName)
	        throws InterruptedException, IOException, KeeperException {
		this.deleteProperty(serviceName, true);
		return true;
	}

}
