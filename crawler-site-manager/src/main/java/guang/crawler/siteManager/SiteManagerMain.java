package guang.crawler.siteManager;

public class SiteManagerMain {
	public static void main(String[] args) throws SiteManagerException {
		SiteManager.me().init().start();
	}
}
