package guang.crawler.connector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 连接Zookeeper的连接器
 *
 * @author sun
 *
 */
public class ZookeeperConnector {
	/**
	 * 底层的Zookeeper连接
	 */
	private ZooKeeper	zookeeper;

	public ZookeeperConnector(final String connectString) throws IOException {
		this.zookeeper = new ZooKeeper(connectString, 3000, null);
	}

	/**
	 * 检查某个节点是否存在,如果不存在,那么创建该节点.
	 *
	 * @param path
	 * @param createMode
	 * @param data
	 * @return
	 * @throws InterruptedException
	 */
	public String checkAndCreateNode(final String path,
	        final CreateMode createMode, final byte[] data)
	        throws InterruptedException {
		boolean exists = false;
		if ((createMode == CreateMode.EPHEMERAL)
		        || (createMode == CreateMode.PERSISTENT)) {
			try {
				exists = this.isNodeExists(path);
			} catch (KeeperException e) {
				e.printStackTrace();
			}
		}
		if (!exists) {
			String realPath = this.createNode(path, createMode, data);
			return realPath;
		}
		return path;
	}

	/**
	 * 创建一个节点
	 *
	 * @param path
	 * @param createMode
	 * @param data
	 * @return
	 * @throws InterruptedException
	 */
	public String createNode(final String path, final CreateMode createMode,
	        final byte[] data) throws InterruptedException {
		try {
			String realPath = this.zookeeper.create(path, data,
			                                        Ids.OPEN_ACL_UNSAFE,
			                                        createMode);
			return realPath;
		} catch (KeeperException e) {
			return null;
		}

	}

	/**
	 * 如果该节点不存在,那么创建该节点,否则更新节点的数据.
	 *
	 * @param path
	 * @param data
	 * @param createMode
	 * @param transaction
	 * @return
	 * @throws InterruptedException
	 */
	public boolean createOrUpdate(final String path, final byte[] data,
	        final CreateMode createMode, final Transaction transaction)
	        throws InterruptedException {
		if (transaction == null) {
			boolean nodeExists = true;
			try {
				this.zookeeper.setData(path, data, -1);
			} catch (KeeperException e) {
				if (e.code() == Code.NONODE) {
					nodeExists = false;
				} else {
					e.printStackTrace();
				}
			}
			if (!nodeExists) {
				try {
					this.zookeeper.create(path, data, Ids.OPEN_ACL_UNSAFE,
					                      createMode);
				} catch (KeeperException e1) {
					return false;
				}
			}
			return true;
		} else {
			transaction.delete(path, -1);
			transaction.create(path, data, Ids.OPEN_ACL_UNSAFE, createMode);
			return true;
		}

	}

	/**
	 * 获取某个节点的所有子节点名称
	 *
	 * @param path
	 * @return
	 * @throws InterruptedException
	 */
	public List<String> getChildren(final String path)
	        throws InterruptedException {
		try {
			return this.zookeeper.getChildren(path, false);
		} catch (KeeperException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取某个节点的数据内容.
	 *
	 * @param path
	 * @return
	 * @throws InterruptedException
	 */
	public byte[] getData(final String path) throws InterruptedException {
		try {
			Stat exists = this.zookeeper.exists(path, false);
			if (exists != null) {
				byte[] data = this.zookeeper.getData(path, false, null);
				return data;
			}
			return null;
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 检查某个节点是否存在.
	 *
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public boolean isNodeExists(final String path) throws KeeperException,
	        InterruptedException {
		boolean nodeExists = false;
		Stat status = this.zookeeper.exists(path, null);
		nodeExists = (status != null);
		return nodeExists;
	}

	/**
	 * 将某个节点递归的移动到另外一个节点.
	 *
	 * @param fromPath
	 * @param toPath
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void moveTo(final String fromPath, final String toPath)
	        throws KeeperException, InterruptedException {
		Transaction transaction = this.zookeeper.transaction();
		LinkedList<String> mvPath = new LinkedList<String>();
		mvPath.add("");
		while (!mvPath.isEmpty()) {
			String first = mvPath.removeFirst();
			String path = fromPath + first;
			byte[] data = this.zookeeper.getData(path, false, null);
			transaction.create(toPath, data, Ids.OPEN_ACL_UNSAFE,
			                   CreateMode.PERSISTENT);
			List<String> children = this.zookeeper.getChildren(path, false);
			if ((children != null) && (children.size() > 0)) {
				for (String child : children) {
					mvPath.add(first + "/" + child);
				}
			}
		}
		this.recursiveDelete(fromPath, transaction);
		transaction.commit();

	}

	/**
	 * 递归的删除某个节点.
	 *
	 * @param path
	 * @param transaction
	 * @return
	 * @throws InterruptedException
	 */
	public boolean recursiveDelete(final String path,
	        final Transaction transaction) throws InterruptedException {

		try {
			List<String> children = this.zookeeper.getChildren(path, false);
			if (children.size() == 0) {
				this.simpleDelete(path, transaction);
				return true;
			} else {
				boolean success = true;
				for (String child : children) {
					success = this.recursiveDelete(path + "/" + child,
					                               transaction);
					if (!success) {
						return false;
					}
				}
				this.simpleDelete(path, transaction);
				return true;
			}
		} catch (KeeperException e) {
			return false;
		}
	}

	/**
	 * 关闭连接
	 * 
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException {
		this.zookeeper.close();
	}

	/**
	 * 简单的删除,而不是递归的删除某个节点.
	 * 
	 * @param path
	 * @param transaction
	 * @return
	 * @throws InterruptedException
	 */
	public boolean simpleDelete(final String path, final Transaction transaction)
	        throws InterruptedException {
		try {
			if (transaction != null) {
				transaction.delete(path, -1);
			} else {
				this.zookeeper.delete(path, -1);
			}
			return true;
		} catch (KeeperException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 创建一个事务.
	 * 
	 * @return
	 */
	public Transaction transaction() {
		return this.zookeeper.transaction();
	}

	/**
	 * 更新某个节点的数据,该节点必须存在.
	 * 
	 * @param path
	 * @param data
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void updateData(final String path, final byte[] data)
	        throws KeeperException, InterruptedException {
		this.zookeeper.setData(path, data, -1);
	}

	/**
	 * 监听某个节点的子节点发生的变化.需要注意的是,该节点自身发生的变化是监控不到的.
	 * 
	 * @param path
	 * @param watcher
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void watchChildren(final String path, final Watcher watcher)
	        throws KeeperException, InterruptedException {
		this.zookeeper.getChildren(path, watcher);
	}

	/**
	 * 监控当前节点发生的变化.需要注意的是,当前节点的子节点发生的变化并不会被监控到.
	 * 
	 * @param path
	 * @param watcher
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void watchNode(final String path, final Watcher watcher)
	        throws KeeperException, InterruptedException {
		this.zookeeper.exists(path, watcher);
	}

}
