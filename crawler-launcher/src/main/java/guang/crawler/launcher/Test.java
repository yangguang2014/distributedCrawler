package guang.crawler.launcher;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException,
			InterruptedException {
		// 既然是模拟，设置一下环境变量
		System.setProperty(
				"crawler.home",
				"/home/sun/work/workspace/distributedCrawler/target/distribute-crawler-1.0-SNAPSHOT-release");
		Main.main(args);
	}
}
