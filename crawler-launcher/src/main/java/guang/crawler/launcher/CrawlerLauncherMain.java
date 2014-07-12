package guang.crawler.launcher;

import java.io.IOException;

/**
 * 主类,用来启动系统
 * 
 * @author sun
 *
 */
public class CrawlerLauncherMain {
	public static void main(final String[] args) throws InterruptedException,
	        IOException {
		
		CrawlerLauncher.me()
		               .init()
		               .launch();
	}
}
