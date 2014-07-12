package guang.crawler.controller;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

/**
 * 启动爬虫控制器的主类
 * 
 * @author sun
 *
 */
public class CrawlerControllerMain {
	
	public static void main(final String[] args) throws InterruptedException,
	        KeeperException, IOException {
		CrawlerController.me()
		                 .init()
		                 .start();
	}
	
}
