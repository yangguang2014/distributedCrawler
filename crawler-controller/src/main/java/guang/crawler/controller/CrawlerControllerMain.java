package guang.crawler.controller;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;

public class CrawlerControllerMain {

	public static void main(String[] args) throws InterruptedException,
			KeeperException, IOException {
		CrawlerController.me().init().start();
	}

}
