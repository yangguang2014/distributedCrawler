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

public class URLsPutter implements Commandlet {
	private final ObjectFilter	urlFilter;

	private Pattern[]	       allowPatterns;

	private Pattern[]	       denyPatterns;
	private byte	           limitDepth;
	
	public URLsPutter() {
		this.urlFilter = SiteManager.me()
		                            .getUrlsFilter();
		Byte wgnlimitDepth = SiteConfig.me()
		                               .getSiteToHandle()
		                               .getWebGatherNodeInfo()
		                               .getWgnDepthLimit();
		if (wgnlimitDepth == null) {
			this.limitDepth = -1;
		} else {
			this.limitDepth = wgnlimitDepth;
		}
		String allowRule = SiteConfig.me()
		                             .getSiteToHandle()
		                             .getWebGatherNodeInfo()
		                             .getWgnAllowRule();
		if ((allowRule != null) && (allowRule.trim()
		                                     .length() != 0)) {
			String[] allowRules = allowRule.split(",");
			this.allowPatterns = new Pattern[allowRules.length];
			for (int i = 0; i < allowRules.length; i++) {
				this.allowPatterns[i] = Pattern.compile(allowRules[i]);
			}
		}
		String denyRule = SiteConfig.me()
		                            .getSiteToHandle()
		                            .getWebGatherNodeInfo()
		                            .getWgnDenyRule();
		if ((denyRule != null) && (denyRule.trim()
		                                   .length() != 0)) {
			String[] denyRules = denyRule.split(",");
			this.denyPatterns = new Pattern[denyRules.length];
			for (int i = 0; i < denyRules.length; i++) {
				this.denyPatterns[i] = Pattern.compile(denyRules[i]);
			}
		}
	}

	@Override
	public DataPacket doCommand(final DataPacket request) {
		
		LinkedList<WebURL> filteredResult = null;
		SiteManager siteManager = null;
		siteManager = SiteManager.me();
		WebURL parent = this.getParentURL(request);
		int count = this.getURLListCount(request);
		if (count > 0) {
			filteredResult = this.filterURLs(request, parent, count);
			this.setAndAddURLs(filteredResult, siteManager);
		}
		if (parent != null) {
			siteManager.getWorkingTaskList()
			           .delete(parent);
			System.out.println("[DELETEED] " + parent.getURL());
		}
		return null;
	}
	
	/**
	 * 根据种种过滤条件过滤掉相关的URL
	 *
	 * @param request
	 * @param parent
	 * @param count
	 * @return
	 */
	private LinkedList<WebURL> filterURLs(final DataPacket request,
	        final WebURL parent, final int count) {
		LinkedList<WebURL> filteredResult;
		filteredResult = new LinkedList<WebURL>();
		for (int i = 0; i < count; i++) {
			String webUrlJson = request.getData()
			                           .get("URL" + i);
			WebURL url = JSON.parseObject(webUrlJson, WebURL.class);
			// 检查该URL是否符合站点的过滤条件
			// 1. 检查该URL的深度是否合法
			boolean success = this.ifAllowByDepth(parent, url);
			if (!success) {
				continue;
			}
			// 2. 检查该URL是否被允许
			boolean allow = this.ifAllowByRules(url);
			if (!allow) {
				continue;
			}
			// 3. 检查该URL是否被拒绝
			boolean deny = this.ifDenyByRules(url);
			if (deny) {
				continue;
			}
			// 4. 检查该URL是否已经重复
			boolean contains = this.urlFilter.containsAndSet(url.getURL());
			if (!contains) {
				filteredResult.add(url);
			}
		}
		return filteredResult;
	}
	
	private WebURL getParentURL(final DataPacket request) {
		String parentJSON = request.getData()
		                           .get("PARENT");
		WebURL parent = null;
		if (parentJSON != null) {
			parent = JSON.parseObject(parentJSON, WebURL.class);
		}
		return parent;
	}
	
	private int getURLListCount(final DataPacket request) {
		String countStr = request.getData()
		                         .get("COUNT");
		int count = 0;
		if (countStr != null) {
			try {
				count = Integer.parseInt(countStr);
			} catch (NumberFormatException e) {
				count = 0;
			}
		}
		return count;
	}
	
	/**
	 * 检查URL的深度是否合法
	 *
	 * @param limitDepth
	 * @param parent
	 * @param current
	 * @return
	 */
	private boolean ifAllowByDepth(final WebURL parent, final WebURL current) {
		// 获取当前深度
		short depth = 0;
		if (parent != null) {
			if (current.isShouldDepthIncrease()) {
				depth = (short) (parent.getDepth() + 1);
			} else {
				depth = parent.getDepth();
			}
			current.setParentDocid(parent.getDocid());
		}
		current.setDepth(depth);
		// 检查深度是否合法
		if ((this.limitDepth < 0) || (depth <= this.limitDepth)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 检查是否被允许
	 *
	 * @param allowPatterns
	 * @param current
	 * @return
	 */
	private boolean ifAllowByRules(final WebURL current) {
		if (this.allowPatterns == null) {
			return true;
		}
		for (Pattern pattern : this.allowPatterns) {
			if (pattern.matcher(current.getURL())
			           .matches()) {
				return true;
			}
		}
		return false;
		
	}

	/**
	 * 检查是否被拒绝
	 *
	 * @param denyPatterns
	 * @param current
	 * @return
	 */
	private boolean ifDenyByRules(final WebURL current) {
		if (this.denyPatterns == null) {
			return false;
		}
		if (this.denyPatterns != null) {
			for (Pattern pattern : this.denyPatterns) {
				if (pattern.matcher(current.getURL())
				           .matches()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 为原始的URL列表添加相关属性，然后加入todo队列中。
	 *
	 * @param filteredResult
	 * @param siteManager
	 */
	private void setAndAddURLs(final LinkedList<WebURL> filteredResult,
	        final SiteManager siteManager) {
		if (filteredResult.size() > 0) {
			DocidServer docidServer = siteManager.getDocidServer();
			String siteManagerId = SiteConfig.me()
			                                 .getSiteManagerInfo()
			                                 .getSiteManagerId();
			String siteId = SiteConfig.me()
			                          .getSiteToHandle()
			                          .getSiteId();
			for (WebURL url : filteredResult) {
				url.setSiteManagerId(siteManagerId);
				url.setSiteId(siteId);
				url.setDocid(docidServer.next(url));
				siteManager.getToDoTaskList()
				           .put(url);
				System.out.println("[ADD] " + url.getURL());
			}
		}
	}
}
