package guang.crawler.siteManager.jsonServer;

public class ServerStartException extends Exception
{
	private static final long	serialVersionUID	= 1L;
	
	public ServerStartException(String msg, Throwable e)
	{
		super(msg, e);
	}
	
}
