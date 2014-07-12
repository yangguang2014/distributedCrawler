package guang.crawler.centerConfig.siteManagers;

import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.commons.GenericState;
import guang.crawler.connector.ZookeeperConnector;
import guang.crawler.util.PathHelper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

/**
 * 该类代表某个站点管理器的配置信息.
 *
 * @author sun
 *
 */
public class SiteManagerInfo extends CenterConfigElement {

	/**
	 * 当前站点管理器的ID
	 */
	private final String	   siteManagerId;
	/**
	 * 属性的key,对应当前站点管理器管理的采集点的ID
	 */
	public static final String	KEY_SITE_TOHANDLE	= "site.toHandle";
	/**
	 * 属性的key,对应当前站点管理器所处的状态,暂时没有真正的使用
	 */
	public static final String	KEY_MANAGER_STATE	= "manager.state";
	/**
	 * 属性的key,对应当前站点管理器是否被分配了采集点
	 */
	public static final String	KEY_DISPATCHED	    = "manager.dispatched";
	/**
	 * 属性的key,对应当前站点管理器的访问地址
	 */
	public static final String	KEY_MANAGER_ADDRESS	= "manager.address";

	/**
	 * 创建一个站点管理器信息对象
	 *
	 * @param path
	 *            当前节点的路径
	 * @param connector
	 *            Zookeeper连接器
	 */
	public SiteManagerInfo(final String path, final ZookeeperConnector connector) {
		super(path, connector);
		this.siteManagerId = PathHelper.getName(path);
	}

	/**
	 * 获取当前节点对应的站点管理器的连接地址
	 *
	 * @return
	 */
	public String getManagerAddress() {
		return this.getProperty(SiteManagerInfo.KEY_MANAGER_ADDRESS);
	}

	/**
	 * 获取当前站点管理器的状态
	 *
	 * @return
	 */
	public GenericState getManagerState() {
		String managerState = this.getProperty(SiteManagerInfo.KEY_MANAGER_STATE);
		if (managerState == null) {
			return GenericState.registed;
		}
		return GenericState.valueOf(managerState);
	}
	
	/**
	 * 获取当前站点管理器的ID
	 *
	 * @return
	 */
	public String getSiteManagerId() {
		return this.siteManagerId;
	}

	/**
	 * 获取当前站点管理器所管理的采集点.
	 *
	 * @return
	 */
	public String getSiteToHandle() {
		return this.getProperty(SiteManagerInfo.KEY_SITE_TOHANDLE);
	}

	/**
	 * 当前站点管理器是否被分配了采集点
	 *
	 * @return
	 */
	public boolean isDispatched() {
		String dispatched = this.getProperty(SiteManagerInfo.KEY_DISPATCHED);
		if (dispatched == null) {
			return false;
		}
		return Boolean.parseBoolean(dispatched);
	}

	/**
	 * 设置当前站点管理器是否被分配了采集点.
	 *
	 * @param dispatched
	 *            当前站点管理器是否被分配了采集点.
	 * @param refreshNow
	 *            是否立刻刷新
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public SiteManagerInfo setDispatched(final boolean dispatched,
	        final boolean refreshNow) throws InterruptedException, IOException,
	        KeeperException {
		this.setProperty(SiteManagerInfo.KEY_DISPATCHED,
		                 String.valueOf(dispatched), refreshNow);
		return this;
	}

	/**
	 * 设置当前站点管理器的连接地址
	 *
	 * @param managerAddress
	 *            站点管理器的连接地址,其格式为:IP:PORT的形式.
	 * @param refreshNow
	 *            是否立刻刷新
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public void setManagerAddress(final String managerAddress,
	        final boolean refreshNow) throws InterruptedException, IOException,
	        KeeperException {
		this.setProperty(SiteManagerInfo.KEY_MANAGER_ADDRESS, managerAddress,
		                 refreshNow);
	}

	/**
	 * 设置当前站点管理器的状态,当前方法并没有被真正的使用到.
	 *
	 * @param managerState
	 *            状态的枚举
	 * @param refreshNow
	 *            是否立刻刷新属性到Zookeeper中.
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public void setManagerState(final GenericState managerState,
	        final boolean refreshNow) throws InterruptedException, IOException,
	        KeeperException {
		this.setProperty(SiteManagerInfo.KEY_MANAGER_STATE,
		                 managerState.toString(), refreshNow);
	}

	/**
	 * 设置当前站点管理器管理的采集点.
	 *
	 * @param siteToHandle
	 *            采集点的ID
	 * @param refreshNow
	 *            是否立刻刷新.
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public SiteManagerInfo setSiteToHandle(final String siteToHandle,
	        final boolean refreshNow) throws InterruptedException, IOException,
	        KeeperException {
		this.setProperty(SiteManagerInfo.KEY_SITE_TOHANDLE, siteToHandle,
		                 refreshNow);
		return this;
	}

}