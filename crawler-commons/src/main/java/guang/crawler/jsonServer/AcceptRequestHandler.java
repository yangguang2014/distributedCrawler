package guang.crawler.jsonServer;

import guang.crawler.localConfig.ComponentLoader;
import guang.crawler.util.StreamHelper;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * 该类对每个连接进行处理.
 *
 * @author yang
 *
 */
public class AcceptRequestHandler implements Runnable {
	/**
	 * 当前连接的套接字
	 */
	private Socket	                     client;
	/**
	 * Commandlet的加载器
	 */
	private ComponentLoader<Commandlet>	 commandletLoader;
	/**
	 * 线程控制器
	 */
	private final AcceptThreadController	acceptThreadController;
	
	/**
	 * 创建一个请求处理器.
	 *
	 * @param client
	 *            客户端套接字
	 * @param commandletLoader
	 *            Commandlet的加载器
	 * @param acceptThreadController
	 *            线程控制器
	 * @throws IOException
	 */
	public AcceptRequestHandler(final Socket client,
	        final ComponentLoader<Commandlet> commandletLoader,
	        final AcceptThreadController acceptThreadController)
	        throws IOException {
		this.client = client;
		this.commandletLoader = commandletLoader;
		this.acceptThreadController = acceptThreadController;
	}
	
	/**
	 * 关闭套接字.
	 */
	private void doExit() {
		try {
			this.client.close();
		} catch (IOException e) {
			return;
		}
	}
	
	/**
	 * 线程主体部分,对一次请求进行处理:获取请求内容,找到处理该请求的类,处理完成后将结果返回给客户端.
	 */
	@Override
	public void run() {
		// 如果当前已经控制要结束了，那么就直接结束。
		if (this.acceptThreadController.getType() != AcceptThreadController.TYPE_START) {
			this.doExit();
			return;
		}
		try {
			// 获取请求内容
			DataPacket data = null;
			try {
				data = StreamHelper.readObject(this.client.getInputStream(),
				                               DataPacket.class);
			} catch (SocketTimeoutException e) {
				return;
			} catch (Exception e) {
				data = null;
			}
			if (data == null) {
				return;
			}
			// 根据请求内容加载相应的Commandlet
			Commandlet commandlet = this.commandletLoader.getComponent(data.getTitle());
			DataPacket result = null;
			// 处理该请求
			if (commandlet == null) {
				result = DataPacket.NOT_FOUND_PACKET;
			} else {
				result = commandlet.doCommand(data);
			}
			// 将处理结果返回
			if (result != null) {
				try {
					StreamHelper.writeObject(this.client.getOutputStream(),
					                         result);
				} catch (IOException e) {
					return;
				}
			}
		} finally {
			this.doExit();
		}
		
	}
}
