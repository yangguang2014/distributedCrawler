package guang.crawler.siteManager;

/**
 * 站点管理器的主类.
 * 
 * @author sun
 *
 */
public class SiteManagerMain {
	public static void main(final String[] args) throws SiteManagerException {
		SiteManager.me()
		           .init()
		           .start();
	}
}
