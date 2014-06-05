package guang.crawler.launcher;

import java.io.IOException;

public class CrawlerLauncherMain {
	public static void main(String[] args) throws InterruptedException,
			IOException {

		CrawlerLauncher.me().init().launch();
	}
}
