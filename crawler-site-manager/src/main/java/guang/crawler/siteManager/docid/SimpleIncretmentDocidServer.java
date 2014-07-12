package guang.crawler.siteManager.docid;

import guang.crawler.commons.WebURL;

/**
 * 简单自增的文档ID生成器
 * 
 * @author sun
 *
 */
@Deprecated
public class SimpleIncretmentDocidServer implements DocidServer {
	private int	id;
	
	@Override
	public synchronized String next(final WebURL webUrl) {
		this.id++;
		return String.valueOf(this.id);
	}
	
}
