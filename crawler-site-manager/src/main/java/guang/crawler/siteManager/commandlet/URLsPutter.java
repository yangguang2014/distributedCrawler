package guang.crawler.siteManager.commandlet;

import guang.crawler.commons.WebURL;
import guang.crawler.jsonServer.Commandlet;
import guang.crawler.jsonServer.DataPacket;
import guang.crawler.siteManager.SiteConfig;
import guang.crawler.siteManager.SiteManager;
import guang.crawler.siteManager.docid.DocidServer;
import guang.crawler.siteManager.urlFilter.ObjectFilter;

import java.util.LinkedList;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;

public class URLsPutter implements Commandlet
{
	private final ObjectFilter	urlFilter;
	
	public URLsPutter()
	{
		this.urlFilter = SiteManager.me().getUrlsFilter();
	}
	
	@Override
	public DataPacket doCommand(DataPacket request)
	{
		
		LinkedList<WebURL> filteredResult = null;
		SiteManager siteManager = null;
		siteManager = SiteManager.me();
		String parentJSON = request.getData().get("PARENT");
		WebURL parent = null;
		if (parentJSON != null)
		{
			parent = JSON.parseObject(parentJSON, WebURL.class);
			siteManager.getWorkingTaskList().delete(parent);
			System.out.println("[DELETEED] " + parent.getURL());
			// 考虑父节点的深度
			byte limitDepth = SiteConfig.me().getSiteToHandle()
			        .getWebGatherNodeInfo().getWgnDepthLimit();
			if (parent.getDepth() == limitDepth)
			{
				// 如果已经达到深度了，那么就直接退出，不再添加新的节点了
				return null;
			}
		}
		String countStr = request.getData().get("COUNT");
		if (countStr != null)
		{
			int count = 0;
			try
			{
				count = Integer.parseInt(countStr);
			} catch (NumberFormatException e)
			{
				return null;
				// TODO 暫時不考虑异常值的情况
			}
			
			if (count > 0)
			{
				filteredResult = new LinkedList<WebURL>();
				String allowRule = SiteConfig.me().getSiteToHandle()
				        .getWebGatherNodeInfo().getWgnAllowRule();
				Pattern[] allowPatterns = null;
				if ((allowRule != null) && (allowRule.trim().length() != 0))
				{
					String[] allowRules = allowRule.split(",");
					allowPatterns = new Pattern[allowRules.length];
					for (int i = 0; i < allowRules.length; i++)
					{
						allowPatterns[i] = Pattern.compile(allowRules[i]);
					}
				}
				String denyRule = SiteConfig.me().getSiteToHandle()
				        .getWebGatherNodeInfo().getWgnDenyRule();
				Pattern[] denyPatterns = null;
				if ((denyRule != null) && (denyRule.trim().length() != 0))
				{
					String[] denyRules = denyRule.split(",");
					denyPatterns = new Pattern[denyRules.length];
					for (int i = 0; i < denyRules.length; i++)
					{
						denyPatterns[i] = Pattern.compile(denyRules[i]);
					}
				}
				for (int i = 0; i < count; i++)
				{
					String webUrlJson = request.getData().get("URL" + i);
					WebURL url = JSON.parseObject(webUrlJson, WebURL.class);
					// 检查该URL是否符合站点的过滤条件
					boolean allow = false;
					if (allowPatterns != null)
					{
						for (Pattern pattern : allowPatterns)
						{
							if (pattern.matcher(url.getURL()).matches())
							{
								allow = true;
								break;
							}
						}
					} else
					{
						allow = true;
					}
					if (!allow)
					{
						continue;
					}
					boolean deny = false;
					if (denyPatterns != null)
					{
						for (Pattern pattern : denyPatterns)
						{
							if (pattern.matcher(url.getURL()).matches())
							{
								deny = true;
								break;
							}
						}
					}
					if (deny)
					{
						continue;
					}
					// TODO 这里要继续处理过滤规则
					boolean contains = this.urlFilter.containsAndSet(url
					        .getURL());
					if (!contains)
					{
						
						if (parent != null)
						{
							short depth = (short) (parent.getDepth() + 1);
							url.setParentDocid(parent.getDocid());
							url.setDepth(depth);
						}
						url.setSiteManagerId(SiteConfig.me()
						        .getSiteManagerInfo().getSiteManagerId());
						url.setSiteId(SiteConfig.me().getSiteToHandle()
						        .getSiteId());
						filteredResult.add(url);
						
					}
				}
				if (filteredResult.size() > 0)
				{
					DocidServer docidServer = siteManager.getDocidServer();
					for (WebURL url : filteredResult)
					{
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
