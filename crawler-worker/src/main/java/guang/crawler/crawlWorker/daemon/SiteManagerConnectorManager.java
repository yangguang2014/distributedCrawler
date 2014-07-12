package guang.crawler.crawlWorker.daemon;

import guang.crawler.centerConfig.CenterConfig;
import guang.crawler.centerConfig.siteManagers.SiteManagerInfo;
import guang.crawler.commons.WebURL;
import guang.crawler.connector.JSONServerConnector;
import guang.crawler.jsonServer.DataPacket;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.alibaba.fastjson.JSON;

/**
 * 由于站点管理器随时都有可能增加或者减少,因此应当及时的更新站点管理器的列表,从而有效的获取URL并处理.这里的方案是监听中央配置器中的相应节点,
 * 在这些节点发生变化时得到通知,从而做出相应的调整.
 *
 * @author sun
 *
 */
public class SiteManagerConnectorManager implements Watcher, Runnable {
	/**
	 * 当前类的单例
	 */
	private static SiteManagerConnectorManager	connectorManager;
	
	/**
	 * 获取单例
	 *
	 * @return
	 */
	public static SiteManagerConnectorManager me() {
		if (SiteManagerConnectorManager.connectorManager == null) {
			SiteManagerConnectorManager.connectorManager = new SiteManagerConnectorManager();
		}
		return SiteManagerConnectorManager.connectorManager;
	}
	
	/**
	 * 缓存的有效的站点管理器的连接
	 */
	private HashMap<String, JSONServerConnector>	     connectors;
	/**
	 * 中央配置器
	 */
	private CenterConfig	                             centerConfig;
	/**
	 * 缓存的有效站点管理器的连接的遍历器
	 */
	private Iterator<Entry<String, JSONServerConnector>>	connectorIterator;
	/**
	 * 事件发生的时间
	 */
	private Date	                                     eventTime	= new Date();
	/**
	 * 管理线程
	 */
	private Thread	                                     managerThread;
	/**
	 * 当前线程是否需要被关闭了
	 */
	private boolean	                                     shutdown	= false;

	private SiteManagerConnectorManager() {
		this.centerConfig = CenterConfig.me();
		this.connectors = new HashMap<String, JSONServerConnector>();
	}

	/**
	 * 结束线程
	 *
	 * @throws IOException
	 */
	public void exit() throws IOException {
		this.shutdown = true;
	}

	/**
	 * 获取当前缓存的连接的数量
	 *
	 * @return
	 */
	public int getSiteManagerConnectorSize() {
		synchronized (this.connectors) {
			return this.connectors.size();
		}

	}

	/**
	 * 获取一个URL
	 *
	 * @return 返回null，表示获取失败，应当再次获取；返回非null表示成功获取了URL；如果当前没有可以使用的连接，那么直接阻塞。
	 * @throws InterruptedException
	 */
	public WebURL getURL() throws InterruptedException {
		// 轮询方式获取下一个可用的站点管理器的连接器
		JSONServerConnector connector = null;
		synchronized (this.connectors) {
			while (this.connectors.size() == 0) {
				this.connectors.wait();
			}
			if ((this.connectorIterator == null)
			        || !this.connectorIterator.hasNext()) {
				this.connectorIterator = this.connectors.entrySet()
				                                        .iterator();
			}
			connector = this.connectorIterator.next()
			                                  .getValue();
		}
		if (connector == null) {
			return null;
		}
		// 向站点管理器发送请求,获取一个URL
		DataPacket data = new DataPacket("/url/get", null, null);
		HashMap<String, String> requestData = new HashMap<String, String>();
		requestData.put("COUNT", "1");
		data.setData(requestData);
		DataPacket result = null;

		boolean success = connector.open();
		if (success) {
			try {
				connector.send(data);
				result = connector.read();
			} catch (IOException e) {
				result = null;
			} finally {
				connector.shutdown();
			}
		}
		// 解析获取的数据并返回
		if (result != null) {
			int count = Integer.parseInt(result.getData()
			                                   .get("COUNT"));
			if (count > 0) {
				String url = result.getData()
				                   .get("URL_LIST" + 0);
				return JSON.parseObject(url, WebURL.class);
			}
		}
		return null;
	}

	/**
	 * 初始化当前类
	 *
	 * @return
	 */
	public SiteManagerConnectorManager init() {
		this.managerThread = new Thread(this, "SiteManagerConnectorDaemon");
		this.managerThread.setDaemon(true);
		return this;
	}

	/**
	 * 处理监听的事件
	 */
	@Override
	public void process(final WatchedEvent event) {
		// 首先继续注册事件监听器
		try {
			if (event.getPath()
			         .equals(CenterConfig.me()
			                             .getWorkersInfo()
			                             .getOnlineWorkers()
			                             .getPath())
			        && (event.getType() != EventType.NodeChildrenChanged)) {
				CenterConfig.me()
				            .getWorkersInfo()
				            .getOnlineWorkers()
				            .watchNode(this);
			} else if (event.getPath()
			                .equals(CenterConfig.me()
			                                    .getSiteManagersConfigInfo()
			                                    .getOnlineSiteManagers())
			        && (event.getType() == EventType.NodeChildrenChanged)) {
				CenterConfig.me()
				            .getSiteManagersConfigInfo()
				            .getOnlineSiteManagers()
				            .watchChildren(this);
			}
		} catch (Exception e) {
			return;
		}
		synchronized (this.eventTime) {
			this.eventTime.setTime(System.currentTimeMillis());
			this.eventTime.notifyAll();
		}

	}
	
	/**
	 * 向站点管理器发送获取的新的URL列表
	 *
	 * @param parent
	 * @param outGoings
	 * @throws IOException
	 */
	public void putData(final WebURL parent, final List<WebURL> outGoings)
	        throws IOException {
		// 准备要发送的数据
		DataPacket request = new DataPacket();
		request.setTitle("/url/put");
		HashMap<String, String> requestData = new HashMap<String, String>();
		requestData.put("PARENT", JSON.toJSONString(parent));
		int size = 0;
		if (outGoings != null) {
			size = outGoings.size();
		}

		int sendSize = 0;
		if (size > 0) {
			for (WebURL url : outGoings) {
				requestData.put("URL" + sendSize++, JSON.toJSONString(url));
			}
		}
		requestData.put("COUNT", String.valueOf(sendSize));
		request.setData(requestData);
		// 获取目的站点管理器
		JSONServerConnector connector = null;
		synchronized (this.connectors) {
			connector = this.connectors.get(parent.getSiteManagerId());
		}
		// 发送
		boolean success = connector.open();
		if (success) {
			try {
				connector.send(request);
			} finally {
				connector.shutdown();
			}
		}

	}

	/**
	 * 更新连接器，更新到最新的事件发生的状态
	 *
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws IOException
	 */
	public void refreshConnectors() throws InterruptedException,
	        KeeperException, IOException {
		synchronized (this.connectors) {
			this.connectors.clear();
			this.connectorIterator = null;
			List<SiteManagerInfo> dispatchedSiteManagers = this.centerConfig.getSiteManagersConfigInfo()
			                                                                .getOnlineSiteManagers()
			                                                                .getAllDispatchedSiteManagers();
			if ((dispatchedSiteManagers != null)
			        && (dispatchedSiteManagers.size() > 0)) {
				for (SiteManagerInfo siteManagerInfo : dispatchedSiteManagers) {
					String siteManagerAddress = siteManagerInfo.getManagerAddress();
					if (siteManagerAddress != null) {
						String[] addInfo = siteManagerAddress.split(":");
						JSONServerConnector connector;

						connector = new JSONServerConnector(addInfo[0],
						        Integer.parseInt(addInfo[1]));
						this.connectors.put(siteManagerInfo.getSiteManagerId(),
						                    connector);
					}
				}
			}
			this.connectors.notifyAll();

		}

	}

	@Override
	public void run() {
		// 注册监听器
		try {
			// 查看workers的通知信息
			CenterConfig.me()
			            .getWorkersInfo()
			            .getOnlineWorkers()
			            .watchNode(this);
			CenterConfig.me()
			            .getSiteManagersConfigInfo()
			            .getOnlineSiteManagers()
			            .watchChildren(this);
		} catch (Exception e) {
			return;
		}
		while (!this.shutdown) {
			Date now = new Date();
			try {
				// 更新所有的连接
				this.refreshConnectors();
			} catch (InterruptedException e) {
				return;
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(1000);
					continue;
				} catch (InterruptedException e1) {
					return;
				}
			}
			// 最后检查一下是否需要继续更新
			synchronized (this.eventTime) {
				if (now.after(this.eventTime)) {
					try {
						this.eventTime.wait();
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}
	}
	
	/**
	 * 启动管理器线程
	 */
	public void start() {
		if (this.managerThread != null) {
			this.managerThread.start();
		}

	}

}
