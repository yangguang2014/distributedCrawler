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
	/**
	 * 页面解析器
	 */
	private Parser	                   parser;
	/**
	 * 页面下载器
	 */
	private PageFetcher	               pageFetcher;
	/**
	 * 下载插件,用来对下载的页面进行处理
	 */
	private LinkedList<DownloadPlugin>	downloadPlugins;

	public PageProcessor() {
		this.parser = new Parser();
		this.pageFetcher = new PageFetcher();
		this.downloadPlugins = new LinkedList<DownloadPlugin>();
	}

	/**
	 * 为页面处理器添加一个插件
	 *
	 * @param plugin
	 */
	public void addPlugin(final DownloadPlugin plugin) {
		this.downloadPlugins.add(plugin);
	}

	/**
	 * 下载指定URL对应的页面
	 *
	 * @param curURL
	 * @return
	 */
	private PageFetchResult download(final WebURL curURL) {
		PageFetchResult fetchResult = null;
		fetchResult = this.pageFetcher.fetchData(curURL);
		if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {
			return fetchResult;
		}
		return null;
	}

	/**
	 * 对下载的内容进行解析
	 *
	 * @param fetchResult
	 * @param curURL
	 * @return
	 */
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
	
	/**
	 * 处理某个指定的URL
	 * 
	 * @param url
	 */
	public void processUrl(final WebURL url) {
		System.out.println("Processing: " + url);
		// 先下载
		PageFetchResult fetchResult = this.download(url);
		// 然后解析
		Page page = this.parse(fetchResult, url);
		// 解析完成后应用插件进行处理
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
	
	/**
	 * 关闭页面处理器
	 */
	public void shutdown() {
		if (this.pageFetcher != null) {
			this.pageFetcher.shutDown();
		}
	}
}
