package guang.crawler.crawlWorker.pageProcessor;

import guang.crawler.commons.Page;

public interface DownloadPlugin
{
	/**
	 * 对下载下来的数据进行相应的处理，返回处理是否成功的信息
	 * 
	 * @param page
	 *            当前处理的页面
	 * @return 返回当前插件处理成功与否
	 */
	public boolean work(Page page);
	
}
