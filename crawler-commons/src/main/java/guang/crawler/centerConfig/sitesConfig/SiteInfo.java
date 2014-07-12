package guang.crawler.centerConfig.sitesConfig;

import guang.crawler.centerConfig.CenterConfigElement;
import guang.crawler.commons.service.WebGatherNodeBean;
import guang.crawler.connector.ZookeeperConnector;
import guang.crawler.util.PathHelper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

import com.alibaba.fastjson.JSON;

/**
 * 某个具体的采集点的信息类
 *
 * @author sun
 *
 */
public class SiteInfo extends CenterConfigElement {
	/**
	 * 属性的key,表示当前采集点是否被处理了
	 */
	private static final String	KEY_HANDLED	      = "handled";
	/**
	 * 属性的key,表示当前采集点所对应的站点管理器的ID
	 */
	private static final String	KEY_SITE_MANAGER	= "siteManager";
	/**
	 * 属性的key,表示当前采集点的配置信息,由舆情分析系统指定了一些配置信息,JSON格式.
	 */
	private static final String	KEY_SITE_CONFIG	  = "siteConfig";
	/**
	 * 属性的key,表示当前节点是否被enabled.
	 */
	private static final String	KEY_SITE_ENABLED	= "site.enabled";
	/**
	 * 属性的key,表示当前节点是否已经采集完成了.
	 */
	private static final String	KEY_SITE_FINISHED	= "site.finished";
	/**
	 * 该站点注册时获取的一个注册ID。
	 */
	private final String	    siteId;
	
	/**
	 * 构造函数,创建一个采集点信息对象
	 *
	 * @param path
	 * @param connector
	 */
	public SiteInfo(final String path, final ZookeeperConnector connector) {
		super(path, connector);
		this.siteId = PathHelper.getName(path);
	}
	
	/**
	 * 获取当前采集点的ID.
	 *
	 * @return
	 */
	public String getSiteId() {
		return this.siteId;
	}
	
	/**
	 * 获取当前节点站点管理器的ID.
	 *
	 * @return
	 * @throws InterruptedException
	 */
	public String getSiteManagerId() throws InterruptedException {
		return this.getProperty(SiteInfo.KEY_SITE_MANAGER);
	}
	
	/**
	 * 获取当前采集点的配置信息.
	 *
	 * @return
	 */
	public WebGatherNodeBean getWebGatherNodeInfo() {
		String configString = this.getProperty(SiteInfo.KEY_SITE_CONFIG);
		if (configString != null) {
			return JSON.parseObject(configString, WebGatherNodeBean.class);
		}
		return null;
	}
	
	/**
	 * 当前节点是否被启动了.
	 *
	 * @return
	 */
	public boolean isEnabled() {
		String enabled = this.getProperty(SiteInfo.KEY_SITE_ENABLED);
		if ("true".equalsIgnoreCase(enabled)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 当前站点是否已经爬取完成了
	 *
	 * @return
	 */
	public boolean isFinished() {
		String finished = this.getProperty(SiteInfo.KEY_SITE_FINISHED);
		if (finished == null) {
			return false;
		}
		return Boolean.parseBoolean(finished);
		
	}
	
	/**
	 * 当前采集点是否被站点管理器处理了.
	 *
	 * @return
	 * @throws InterruptedException
	 */
	public boolean isHandled() throws InterruptedException {
		String handled = this.getProperty(SiteInfo.KEY_HANDLED);
		if (handled == null) {
			return false;
		}
		return Boolean.parseBoolean(handled);
	}
	
	/**
	 * 设置当前采集点是否enabled.
	 * 
	 * @param enabled
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public SiteInfo setEnabled(final boolean enabled)
	        throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteInfo.KEY_SITE_ENABLED, Boolean.toString(enabled),
		                 true);
		return this;
	}
	
	/**
	 * 设置当前采集点是否已经采集完成了,已经采集完成的节点是不会被分配给站点管理器的.
	 * 
	 * @param finished
	 * @param refreshNow
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public SiteInfo setFinished(final boolean finished, final boolean refreshNow)
	        throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteInfo.KEY_SITE_FINISHED,
		                 Boolean.toString(finished), refreshNow);
		return this;
	}
	
	/**
	 * 设置当前采集点是否已经被处理了.已经处理的采集点不能被分配给站点管理器了.
	 * 
	 * @param handled
	 * @param refreshNow
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public SiteInfo setHandled(final boolean handled, final boolean refreshNow)
	        throws InterruptedException, IOException, KeeperException {
		this.setProperty(SiteInfo.KEY_HANDLED, String.valueOf(handled),
		                 refreshNow);
		return this;
	}
	
	/**
	 * 设置当前采集点所对应的站点管理器的ID.
	 * 
	 * @param siteManagerId
	 * @param refreshNow
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public SiteInfo setSiteManagerId(final String siteManagerId,
	        final boolean refreshNow) throws InterruptedException, IOException,
	        KeeperException {
		this.setProperty(SiteInfo.KEY_SITE_MANAGER, siteManagerId, refreshNow);
		return this;
	}
	
	/**
	 * 设置当前采集点的配置信息.
	 * 
	 * @param info
	 * @param refreshNow
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public SiteInfo setWebGatherNodeInfo(final WebGatherNodeBean info,
	        final boolean refreshNow) throws InterruptedException, IOException,
	        KeeperException {
		if (info != null) {
			String configString = JSON.toJSONString(info);
			this.setProperty(SiteInfo.KEY_SITE_CONFIG, configString, refreshNow);
		}
		return this;
	}
	
}
