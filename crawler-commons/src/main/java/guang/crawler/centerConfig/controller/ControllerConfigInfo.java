package guang.crawler.centerConfig.controller;

import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

/**
 * 控制器配置信息.
 * <p>
 * 该类用来表示Zookeeper中控制器节点,处理系统中控制器的各项配置需求,主要包括:竞争控制器角色,添加services等.
 * </p>
 *
 * @author sun
 *
 */
public class ControllerConfigInfo extends CenterConfigElement {
	/**
	 * 当前路径下manager节点的路径,竞争控制器成功的节点将创建该节点
	 */
	private static final String	   MANAGER	= "/manager";
	/**
	 * 当前路径下services节点的路径,竞争控制器成功的节点将创建该节点,存储其发布的服务信息.
	 */
	private static final String	   SERVICES	= "/services";
	/**
	 * 当前路径下manager节点对应的类对象
	 */
	private ControllerManagerInfo	controllerManagerInfo;
	/**
	 * 当前节点下services节点对应的类对象
	 */
	private ControllerServicesInfo	controllerServicesInfo;

	public ControllerConfigInfo(final String path,
	        final ZookeeperConnector connector) {
		super(path, connector);
	}

	/**
	 * 竞争作为控制器
	 *
	 * @param addr
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public boolean competeForController() throws InterruptedException,
	        KeeperException {
		String realPath = this.connector.createNode(this.path
		                                                    + ControllerConfigInfo.MANAGER,
		                                            CreateMode.EPHEMERAL,
		                                            "comment=manager of the controller".getBytes());
		if (realPath != null) {
			this.controllerManagerInfo = new ControllerManagerInfo(this,
			        realPath, this.connector);
			String servicePath = this.path + ControllerConfigInfo.SERVICES;
			if (this.connector.isNodeExists(servicePath)) {
				this.connector.simpleDelete(servicePath, null);
			}
			this.connector.createNode(servicePath,
			                          CreateMode.EPHEMERAL,
			                          "comment=web services managed by controller".getBytes());
			this.controllerServicesInfo = new ControllerServicesInfo(
			        servicePath, this.connector);
			return true;
		}
		return false;
	}

	/**
	 * 获取控制器的管理者信息
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws IOException
	 */
	public ControllerManagerInfo getControllerManagerInfo()
	        throws KeeperException, InterruptedException, IOException {
		if (this.controllerManagerInfo == null) {
			this.controllerManagerInfo = new ControllerManagerInfo(this,
			        this.path + ControllerConfigInfo.MANAGER, this.connector);
			if (this.controllerManagerInfo.exists()) {
				this.controllerManagerInfo.load();
			}
		}
		return this.controllerManagerInfo;
	}

	/**
	 * 获取控制器信息中的服务信息
	 * 
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public ControllerServicesInfo getControllerServicesInfo()
	        throws KeeperException, InterruptedException, IOException {

		if (this.controllerServicesInfo == null) {
			this.controllerServicesInfo = new ControllerServicesInfo(this.path
			        + ControllerConfigInfo.SERVICES, this.connector);
			if (this.controllerServicesInfo.exists()) {
				this.controllerServicesInfo.load();
			}
		}
		return this.controllerServicesInfo;
	}

}
