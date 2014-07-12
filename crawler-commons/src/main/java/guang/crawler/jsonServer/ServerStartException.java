package guang.crawler.jsonServer;

/**
 * 异常类,JSON服务器启动失败时抛出
 * 
 * @author sun
 *
 */
public class ServerStartException extends Exception {
	private static final long	serialVersionUID	= 1L;

	public ServerStartException(final String msg, final Throwable e) {
		super(msg, e);
	}

}
