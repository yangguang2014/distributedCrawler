package guang.crawler.siteManager.commandlet;

import guang.crawler.core.WebURL;
import guang.crawler.jsonServer.Commandlet;
import guang.crawler.jsonServer.DataPacket;
import guang.crawler.siteManager.SiteConfig;
import guang.crawler.siteManager.SiteManager;
import guang.crawler.siteManager.docid.DocidServer;
import guang.crawler.siteManager.urlFilter.BitMapFilter;
import guang.crawler.siteManager.urlFilter.ObjectFilter;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import com.alibaba.fastjson.JSON;

public class URLsPutter implements Commandlet {
	private final ObjectFilter urlFilter;

	public URLsPutter() throws NoSuchAlgorithmException {
		this(new BitMapFilter());
	}

	public URLsPutter(ObjectFilter urlFilter) {
		this.urlFilter = urlFilter;
	}

	@Override
	public DataPacket doCommand(DataPacket request) {
		String countStr = request.getData().get("COUNT");
		LinkedList<WebURL> filteredResult = null;
		SiteManager siteManager = null;
		siteManager = SiteManager.me();
		String parentJSON = request.getData().get("PARENT");
		if (parentJSON != null) {
			WebURL parent = JSON.parseObject(parentJSON, WebURL.class);
			siteManager.getWorkingTaskList().delete(parent);
			System.out.println("[DELETEED] " + parent.getURL());
		}
		if (countStr != null) {
			int count = 0;
			try {
				count = Integer.parseInt(countStr);
			} catch (NumberFormatException e) {
				return null;
				// TODO 暫時不考虑异常值的情况
			}

			if (count > 0) {
				filteredResult = new LinkedList<>();
				for (int i = 0; i < count; i++) {
					String webUrlJson = request.getData().get("URL" + i);
					WebURL url = JSON.parseObject(webUrlJson, WebURL.class);
					boolean contains = this.urlFilter.containsAndSet(url
							.getURL());
					if (!contains) {
						url.setSiteManagerName(SiteConfig.me()
								.getSiteManagerId());
						filteredResult.add(url);

					}
				}
				if (filteredResult.size() > 0) {
					DocidServer docidServer = siteManager.getDocidServer();
					for (WebURL url : filteredResult) {
						url.setDocid(docidServer.next(url));
						siteManager.getToDoTaskList().put(url);
						System.out.println("[ADD] " + url.getURL());
					}
				}
			}

		}
		return null;
	}

}
