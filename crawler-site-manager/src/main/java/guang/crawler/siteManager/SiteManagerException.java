package guang.crawler.siteManager;

/**
 * 异常类,站点管理器出现的异常
 *
 * @author sun
 *
 */
public class SiteManagerException extends Exception {
	private static final long	serialVersionUID	= 1L;
	
	public SiteManagerException(final String message) {
		super(message);
	}
	
	public SiteManagerException(final String message, final Throwable e) {
		super(message, e);
	}
}
