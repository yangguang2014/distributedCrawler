package guang.crawler.centerConfig.controller;

import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

/**
 * 该类对应了控制器配置节点下的manager节点,竞争控制器成功的线程将创建该节点,并在该节点中记录当前线程所对应主机的IP
 *
 * @author sun
 *
 */
public class ControllerManagerInfo extends CenterConfigElement {
	/**
	 * 当前manager节点的父节点,该变量设置的目的是为了方便的访问.
	 */
	private final ControllerConfigInfo	controllerConfigInfo;

	/**
	 * 该节点中存储了管理者的主机的地址,该字符串即是表示改属性的key.
	 */
	private static final String	       KEY_CONTROLLER_MANAGER_ADDR	= "controller.manager.address";

	/**
	 * 创建控制器节点下的管理者对象.
	 *
	 * @param controllerConfigInfo
	 *            代表当前节点的父节点的对象,以方便双向访问.
	 * @param path
	 *            当前节点的路径
	 * @param connector
	 *            Zookeeper连接器
	 */
	public ControllerManagerInfo(
	        final ControllerConfigInfo controllerConfigInfo, final String path,
	        final ZookeeperConnector connector) {
		super(path, connector);
		this.controllerConfigInfo = controllerConfigInfo;
	}

	public ControllerConfigInfo getControllerConfigInfo() {
		return this.controllerConfigInfo;
	}

	/**
	 * 获取manager节点的拥有者的主机的IP
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public String getControllerManagerAddress() throws InterruptedException {
		return this.getProperty(ControllerManagerInfo.KEY_CONTROLLER_MANAGER_ADDR);
	}
	
	/**
	 * 设置manager节点的拥有者的主机的IP
	 * 
	 * @param address
	 *            IP地址
	 * @param refreshNow
	 *            是否立即刷新
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public void setControllerManagerAddress(final String address,
	        final boolean refreshNow) throws InterruptedException, IOException,
	        KeeperException {
		this.setProperty(ControllerManagerInfo.KEY_CONTROLLER_MANAGER_ADDR,
		                 address, refreshNow);
	}
}