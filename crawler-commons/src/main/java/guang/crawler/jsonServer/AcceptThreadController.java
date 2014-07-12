package guang.crawler.jsonServer;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 对JSON服务器线程池中的线程进行控制.主要是希望在需要关闭线程的时候,这些线程都能够直接返回,不工作了.
 *
 * @author sun
 *
 */
public class AcceptThreadController {
	/**
	 * 控制类型为:立刻关闭
	 */
	public static final byte	   TYPE_SHUTDOWN_NOW	    = 0;
	/**
	 * 控制类型为:合理的关闭
	 */
	public static final byte	   TYPE_SHUTDOWN_GRACEFULLY	= 1;
	/**
	 * 控制类型为: 正在运行
	 */
	public static final byte	   TYPE_START	            = 2;
	/**
	 * 控制类型
	 */
	private byte	               type;
	private ReentrantReadWriteLock	lock	                = new ReentrantReadWriteLock();
	
	/**
	 * 创建一个线程控制器,默认的控制类型为{@link #TYPE_START}
	 */
	public AcceptThreadController() {
		this.type = AcceptThreadController.TYPE_START;
	}
	
	/**
	 * 获取当前的控制类型
	 * 
	 * @return
	 */
	public byte getType() {
		this.lock.readLock()
		         .lock();
		byte result = this.type;
		this.lock.readLock()
		         .unlock();
		return result;
	}
	
	/**
	 * 设置当前的控制类型
	 * 
	 * @param type
	 */
	public void setType(final byte type) {
		this.lock.writeLock()
		         .lock();
		this.type = type;
		this.lock.writeLock()
		         .unlock();
	}
	
}
