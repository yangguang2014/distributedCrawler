package guang.crawler.jsonServer;

/**
 * 用来对一个请求数据包进行处理的类
 *
 * @author sun
 *
 */
public interface Commandlet {
	/**
	 * 处理一个请求包,然后返回处理结果
	 * 
	 * @param request
	 * @return
	 */
	public DataPacket doCommand(DataPacket request);
	
}
