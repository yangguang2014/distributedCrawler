package guang.crawler.connector;

import guang.crawler.jsonServer.DataPacket;
import guang.crawler.util.StreamHelper;

import java.io.IOException;
import java.net.Socket;

/**
 * 连接站点管理器启动的JSON 服务器的连接器.
 *
 * @author sun
 *
 */
public class JSONServerConnector {

	/**
	 * 底层套接字
	 */
	private Socket	socket;
	/**
	 * 主机名
	 */
	private String	host;
	/**
	 * 端口号
	 */
	private int	   port;

	/**
	 * 创建一个JSON服务器的连接器
	 *
	 * @param host
	 * @param port
	 */
	public JSONServerConnector(final String host, final int port) {
		this.host = host;
		this.port = port;

	}

	/**
	 * 打开连接.在进行其他操作之前必须先进行当前操作.
	 *
	 * @return
	 */
	public boolean open() {
		try {
			this.socket = new Socket(this.host, this.port);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 从JSON服务器中读取一个数据包.
	 *
	 * @return
	 * @throws IOException
	 */
	public DataPacket read() throws IOException {
		return StreamHelper.readObject(this.socket.getInputStream(),
		                               DataPacket.class);
	}

	/**
	 * 向JSON服务器发送一个数据包.
	 *
	 * @param packet
	 * @throws IOException
	 */
	public void send(final DataPacket packet) throws IOException {
		StreamHelper.writeObject(this.socket.getOutputStream(), packet);
	}

	/**
	 * 关闭连接.
	 */
	public void shutdown() {
		if (this.socket != null) {
			try {
				this.socket.close();
			} catch (IOException e) {
				// skip
			}
		}
	}

}
