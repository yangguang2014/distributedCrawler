package guang.crawler.siteManager.docid;

import guang.crawler.commons.WebURL;

public class SimpleIncretmentDocidServer implements DocidServer
{
	private int	id;
	
	@Override
	public synchronized String next(WebURL webUrl)
	{
		this.id++;
		return String.valueOf(this.id);
	}
	
}
