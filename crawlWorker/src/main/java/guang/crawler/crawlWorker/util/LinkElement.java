package guang.crawler.crawlWorker.util;

import java.util.HashMap;

/**
 * 需要从中获取URL的元素的名称。
 * 
 * @author yang
 */
public enum LinkElement
{
	A, AREA, LINK, IFRAME, FRAME, EMBED, IMG, BASE, META, BODY;
	private static HashMap<String, LinkElement>	elements;
	
	public static LinkElement getElement(String name)
	{
		if (LinkElement.elements == null)
		{
			LinkElement.elements = new HashMap<>();
			LinkElement[] values = LinkElement.values();
			for (LinkElement value : values)
			{
				LinkElement.elements.put(value.name(), value);
			}
		}
		return LinkElement.elements.get(name.toUpperCase());
	}
}