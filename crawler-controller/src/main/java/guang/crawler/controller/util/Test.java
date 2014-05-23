package guang.crawler.controller.util;

import java.util.Iterator;

public class Test {
	public static void main(String[] args) {
		Iterator<String> it = System.getenv().keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();

			System.out.println(key + ":" + System.getenv(key));
		}
	}
}
