package guang.crawler.centerConfig;

import guang.crawler.connector.ZookeeperConnector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.Watcher;

/**
 * 该类表示中央配置器中的每个节点.可以对该节点进行增删改查,加锁解锁,监听.
 * <p>
 * 每个节点中的内容是相同的:都是基于Java Properties类的key value属性.
 * </p>
 *
 * @author sun
 *
 */
public abstract class CenterConfigElement {
	/**
	 * 当前节点的路径
	 */
	protected final String	           path;
	/**
	 * 底层的Zookeeper连接器
	 */
	protected final ZookeeperConnector	connector;
	/**
	 * 当前节点中存储的属性.
	 */
	private Properties	               values;
	/**
	 * 当前节点的锁的路径.每个节点都可以有一个锁,从而控制对节点的并发访问.
	 */
	private static final String	       PATH_LOCK	      = "_lock";
	/**
	 * 如果有进程向该节点发送通知,通知信息将写在当前节点属性中,属性的名称即为当前变量.
	 */
	private static final String	       KEY_NOTIFY_CHANGED	= "notify.changed";

	/**
	 * 创建一个配置器元素,需要传入代表该节点的路径,以及Zookeeper连接器.
	 *
	 * @param path
	 * @param connector
	 */
	public CenterConfigElement(final String path,
	        final ZookeeperConnector connector) {
		this.path = path;
		this.connector = connector;
		this.values = new Properties();
	}

	/**
	 * 删除当前节点
	 *
	 * @param transaction
	 * @return
	 * @throws InterruptedException
	 */
	public boolean delete(final Transaction transaction)
	        throws InterruptedException {
		return this.connector.recursiveDelete(this.path, transaction);
	}

	/**
	 * 从当前节点中删除某个属性
	 *
	 * @param key
	 * @param refreshNow
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public void deleteProperty(final String key, final boolean refreshNow)
	        throws InterruptedException, IOException, KeeperException {
		this.values.remove(key);
		if (refreshNow) {
			this.update();
		}
	}

	/**
	 * 判断当前节点是否存在
	 *
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public boolean exists() throws KeeperException, InterruptedException {
		return this.connector.isNodeExists(this.path);
	}

	/**
	 * 获取当前节点的路径
	 *
	 * @return
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * 获取当前节点中的所有属性
	 *
	 * @return
	 */
	public Properties getProperties() {
		return this.values;
	}

	/**
	 * 根据key获取当前节点的属性
	 *
	 * @param key
	 * @return
	 */
	public String getProperty(final String key) {
		return this.values.getProperty(key);
	}

	/**
	 * 从Zookeeper中加载该节点.在获取当前节点相应的属性之前应当调用该方法.另外,如果需要获取最新的Zookeeper中的数据,
	 * 也需要调用当前方法.
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public boolean load() throws InterruptedException, IOException {
		byte[] data = this.connector.getData(this.path);
		if (data != null) {
			this.values.load(new ByteArrayInputStream(data));
		}
		return true;
	}

	/**
	 * 对当前节点进行锁定
	 *
	 * @return
	 */
	public boolean lock() {
		// TODO 这里应当仔细的检查该锁是否已经被当前线程获取了。
		try {
			String realPath = this.connector.createNode(this.path
			                                                    + CenterConfigElement.PATH_LOCK,
			                                            CreateMode.EPHEMERAL,
			                                            Long.toString(Thread.currentThread()
			                                                                .getId())
			                                                .getBytes());
			if (realPath == null) {
				return false;
			} else {
				return true;
			}

		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * 通知当前节点有事件发生了.目前暂时不支持设置事件的种类和内容,只是通知一下.被通知的一方可以从阻塞状态中恢复,检测具体是什么样的事件发生了.
	 *
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public void notifyChanged() throws InterruptedException, IOException,
	        KeeperException {
		this.setProperty(CenterConfigElement.KEY_NOTIFY_CHANGED,
		                 new Date().toString(), true);
	}

	/**
	 * 向当前节点中设置某个属性
	 *
	 * @param key
	 * @param value
	 * @param refreshNow
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public void setProperty(final String key, final String value,
	        final boolean refreshNow) throws InterruptedException, IOException,
	        KeeperException {
		this.values.put(key, value);
		if (refreshNow) {
			this.update();
		}

	}

	/**
	 * 解除对当前节点的锁定
	 *
	 * @return
	 */
	public boolean unlock() {
		try {
			return this.connector.simpleDelete(this.path
			        + CenterConfigElement.PATH_LOCK, null);
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * 将当前节点的数据存储到Zookeeper中,更新Zookeeper目录树.
	 *
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws KeeperException
	 */
	public boolean update() throws InterruptedException, IOException,
	        KeeperException {
		if (!this.exists()) {
			return false;
		}
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		this.values.store(byteOut, "update at " + new Date().toString());
		byte[] data = byteOut.toByteArray();
		try {
			this.connector.updateData(this.path, data);
		} finally {
			byteOut.close();
		}
		return true;
	}

	/**
	 * 对当前节点的子节点进行监听,当前节点的子节点被删除或者增加时,监听的线程将得到通知.
	 *
	 * @param watcher
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void watchChildren(final Watcher watcher) throws KeeperException,
	        InterruptedException {
		this.connector.watchChildren(this.path, watcher);
	}
	
	/**
	 * 对当前节点进行监听,当前节点创建,删除,数据更改都将触发事件,监听的线程将得到通知.
	 * 
	 * @param watcher
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void watchNode(final Watcher watcher) throws KeeperException,
	        InterruptedException {
		this.connector.watchNode(this.path, watcher);
	}
}
