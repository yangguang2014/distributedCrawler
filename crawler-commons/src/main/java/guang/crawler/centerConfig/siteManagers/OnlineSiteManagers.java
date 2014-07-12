package guang.crawler.centerConfig.siteManagers;

import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.commons.GenericState;
import guang.crawler.connector.ZookeeperConnector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

/**
 * 代表所有在线的站点管理器的类
 *
 * @author sun
 *
 */
public class OnlineSiteManagers extends CenterConfigElement {
	
	/**
	 * 创建在线站点管理器对象
	 * 
	 * @param path
	 *            当前节点的路径
	 * @param connector
	 *            Zookeeper连接器
	 */
	public OnlineSiteManagers(final String path,
	        final ZookeeperConnector connector) {
		super(path, connector);
	}
	
	/**
	 * 获取所有已经分配的站点管理器
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public List<SiteManagerInfo> getAllDispatchedSiteManagers()
	        throws InterruptedException, KeeperException {
		LinkedList<SiteManagerInfo> result = new LinkedList<SiteManagerInfo>();
		List<String> children = this.connector.getChildren(this.path);
		for (String child : children) {
			try {
				SiteManagerInfo siteManagerInfo = this.getSiteManagerInfo(child);
				
				if ((siteManagerInfo != null) && siteManagerInfo.isDispatched()) {
					result.add(siteManagerInfo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return result;
	}
	
	/**
	 * 获取所有已经分配的站点管理器
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public List<SiteManagerInfo> getAllSiteManagers()
	        throws InterruptedException, KeeperException {
		LinkedList<SiteManagerInfo> result = new LinkedList<SiteManagerInfo>();
		List<String> children = this.connector.getChildren(this.path);
		for (String child : children) {
			try {
				SiteManagerInfo siteManagerInfo = this.getSiteManagerInfo(child);
				
				result.add(siteManagerInfo);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return result;
	}
	
	/**
	 * 获取所有尚未分配的站点管理器
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public LinkedList<SiteManagerInfo> getAllUndispatchedSiteManagers()
	        throws InterruptedException, KeeperException {
		LinkedList<SiteManagerInfo> result = new LinkedList<SiteManagerInfo>();
		List<String> children = this.connector.getChildren(this.path);
		for (String child : children) {
			try {
				SiteManagerInfo siteManagerInfo = this.getSiteManagerInfo(child);
				if ((siteManagerInfo != null)
				        && !siteManagerInfo.isDispatched()) {
					result.add(siteManagerInfo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return result;
	}
	
	/**
	 * 根据site-manager的ID获取站点信息
	 *
	 * @param siteManagerId
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	public SiteManagerInfo getSiteManagerInfo(final String siteManagerId)
	        throws InterruptedException, IOException, KeeperException {
		String realPath = this.path + "/" + siteManagerId;
		boolean exist = this.connector.isNodeExists(realPath);
		if (!exist) {
			return null;
		}
		SiteManagerInfo siteManagerInfo = new SiteManagerInfo(realPath,
		        this.connector);
		siteManagerInfo.load();
		return siteManagerInfo;
	}
	
	/**
	 * 新增了一个站点管理器节点
	 *
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws IOException
	 */
	public SiteManagerInfo registSiteManager() throws InterruptedException,
	        IOException, KeeperException {
		String realPath = this.connector.createNode(this.path + "/site-manager",
		                                            CreateMode.EPHEMERAL_SEQUENTIAL,
		                                            "".getBytes());
		if (realPath != null) {
			SiteManagerInfo managerInfo = new SiteManagerInfo(realPath,
			        this.connector);
			managerInfo.setManagerState(GenericState.registed, true);
			return managerInfo;
		}
		return null;
	}
}
