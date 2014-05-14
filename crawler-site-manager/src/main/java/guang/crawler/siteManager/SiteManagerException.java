package guang.crawler.siteManager;

public class SiteManagerException extends Exception
{
	private static final long	serialVersionUID	= 1L;
	
	public SiteManagerException(String message)
	{
		super(message);
	}
	
	public SiteManagerException(String message, Throwable e)
	{
		super(message, e);
	}
}
