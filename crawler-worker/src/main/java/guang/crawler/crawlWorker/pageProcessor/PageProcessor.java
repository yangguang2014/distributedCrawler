package guang.crawler.crawlWorker.pageProcessor;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.crawlWorker.fetcher.PageFetchResult;
import guang.crawler.crawlWorker.fetcher.PageFetcher;
import guang.crawler.crawlWorker.parser.Parser;

import java.util.LinkedList;

import org.apache.http.HttpStatus;

/**
 * 该类用来处理页面的下载,解析以及后续处理
 *
 * @author sun
 *
 */
public class PageProcessor {
	
	private Parser	                   parser;
	private PageFetcher	               pageFetcher;
	private LinkedList<DownloadPlugin>	downloadPlugins;
	
	public PageProcessor() {
		this.parser = new Parser();
		this.pageFetcher = new PageFetcher();
		this.downloadPlugins = new LinkedList<DownloadPlugin>();
	}

	public void addPlugin(final DownloadPlugin plugin) {
		this.downloadPlugins.add(plugin);
	}

	private PageFetchResult download(final WebURL curURL) {
		PageFetchResult fetchResult = null;
		fetchResult = this.pageFetcher.fetchData(curURL);
		if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {
			return fetchResult;
		}
		return null;
	}

	private Page parse(final PageFetchResult fetchResult, final WebURL curURL) {
		try {
			Page page = new Page(curURL);
			fetchResult.transformToPage(page);
			if (this.parser.parse(page, curURL.getURL())) {
				return page;
			}
		} catch (Exception e) {
			return null;
		} finally {
			if (fetchResult != null) {
				fetchResult.discardContentIfNotConsumed();
			}
		}
		return null;
		
	}

	public void processUrl(final WebURL url) {
		System.out.println("Processing: " + url);
		PageFetchResult fetchResult = this.download(url);
		Page page = this.parse(fetchResult, url);
		if (page != null) {
			for (DownloadPlugin plugin : this.downloadPlugins) {
				boolean success = plugin.work(page);
				if (!success) {
					break;
				}
			}
		} else {
			System.out.println("Couldn't fetch the content of the page.");
		}
	}

	public void shutdown() {
		if (this.pageFetcher != null) {
			this.pageFetcher.shutDown();
		}
	}
}
