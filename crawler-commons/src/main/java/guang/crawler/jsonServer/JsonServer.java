package guang.crawler.jsonServer;

import java.net.InetAddress;

/**
 * 实现了JSON协议通信的服务器
 * 
 * @author sun
 *
 */
public interface JsonServer {

	/**
	 * 获取当前服务器的主机
	 * 
	 * @return
	 */
	public InetAddress getAddress();

	/**
	 * 获取当前服务器的端口号
	 * 
	 * @return
	 */
	public int getPort();

	/**
	 * 当前主机是否已经关闭
	 * 
	 * @return
	 */
	public boolean isShutdown();

	/**
	 * 关闭当前主机
	 */
	public void shutdown();

	/**
	 * 启动当前主机
	 * 
	 * @return
	 */
	public boolean start();

	/**
	 * 等待当前主机关闭,在此之前等待.
	 */
	public void waitForStop();

}