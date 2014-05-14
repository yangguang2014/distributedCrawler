package guang.crawler.siteManager.docid;

import guang.crawler.core.WebURL;

/**
 * 生成docid的服务器接口
 * 
 * @author yang
 * 
 */
public interface DocidServer
{
	/**
	 * 根据该webUrl获取下一个可用的id
	 * 
	 * @param webUrl
	 * @return
	 */
	public String next(WebURL webUrl);
}
