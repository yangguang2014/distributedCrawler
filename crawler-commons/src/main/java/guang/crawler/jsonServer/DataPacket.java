package guang.crawler.jsonServer;

import java.util.HashMap;

/**
 * 数据包,JSON服务器传输过程中使用的数据类型.
 *
 * @author sun
 *
 */
public class DataPacket {
	/**
	 * 特殊的数据包:退出
	 */
	public static final DataPacket	EXIT_DATA_PACKET	= new DataPacket(
	                                                         "/exit", null,
	                                                         null);
	/**
	 * 特殊的数据包: 请求内容未找到
	 */
	public static final DataPacket	NOT_FOUND_PACKET	= new DataPacket(
	                                                         "/notFound", null,
	                                                         null);

	/**
	 * 传输的主题,根据该主题找到对应的Commandlet
	 */
	private String	                title;
	/**
	 * 传输的参数
	 */
	private HashMap<String, String>	arguments;
	/**
	 * 传输的数据
	 */
	private HashMap<String, String>	data;

	public DataPacket() {
		// 默认构造函数
	}

	/**
	 * 创建一个传输数据包
	 *
	 * @param title
	 *            数据包的主题
	 * @param arguments
	 *            数据包的参数
	 * @param data
	 *            数据包的数据
	 */
	public DataPacket(final String title,
	        final HashMap<String, String> arguments,
	        final HashMap<String, String> data) {
		this.title = title;
		this.arguments = arguments;
		this.data = data;
	}

	/**
	 * 获取数据包的请求参数
	 * 
	 * @return
	 */
	public HashMap<String, String> getArguments() {
		return this.arguments;
	}

	/**
	 * 获取数据包的数据内容
	 * 
	 * @return
	 */
	public HashMap<String, String> getData() {
		return this.data;
	}

	/**
	 * 获取数据包的主题
	 * 
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * 设置数据包的参数
	 * 
	 * @param arguments
	 */
	public void setArguments(final HashMap<String, String> arguments) {
		this.arguments = arguments;
	}

	/**
	 * 设置数据包的数据内容
	 * 
	 * @param data
	 */
	public void setData(final HashMap<String, String> data) {
		this.data = data;
	}
	
	/**
	 * 设置数据包的主题
	 * 
	 * @param title
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

}
