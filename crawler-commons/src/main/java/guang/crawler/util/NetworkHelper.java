package guang.crawler.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 用来帮助网络操作的类
 *
 * @author sun
 *
 */
public class NetworkHelper {
	
	/**
	 * 获取当前主机的IP地址
	 *
	 * @return
	 * @throws UnknownHostException
	 */
	public static String getIPAddress() throws UnknownHostException {
		return InetAddress.getLocalHost()
		                  .getHostAddress();
	}
	
	/**
	 * 获取当前主机的主机名
	 *
	 * @return
	 * @throws UnknownHostException
	 */
	public static String getLocalHostName() throws UnknownHostException {
		return InetAddress.getLocalHost()
		                  .getCanonicalHostName();
	}
	
	/**
	 * 检测某个端口是否可用
	 *
	 * @param port
	 * @return
	 */
	public static synchronized boolean isPortAvailable(final int port) {
		Socket socket = new Socket();
		try {
			socket.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
		
		return true;
	}
	
	/**
	 * 获取下一个可用的端口
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public synchronized static int nextAvailablePort(final int from,
	        final int to) {
		for (int i = from; i < to; i++) {
			int port = i;
			if (NetworkHelper.isPortAvailable(port)) {
				return port;
			}
		}
		return -1;
	}
}
