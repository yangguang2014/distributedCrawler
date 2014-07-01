package guang.crawler.extension.urlExtractor.qq;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.commons.parserData.HtmlParseData;
import guang.crawler.commons.parserData.ParseData;
import guang.crawler.extension.urlExtractor.URLsExtractor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取QQ新闻页面中应当获取的所有URL，包括静态链接URL,第一条评论的URL地址,以及评论数等信息。
 *
 * @author sun
 *
 */
public class QQNewsURLsExtractor implements URLsExtractor {

	private Pattern	cmtIdPattern;

	public QQNewsURLsExtractor() {
		this.cmtIdPattern = Pattern.compile("cmt_id\\s*=\\s*([0-9]+)\\s*;");
	}

	@Override
	public void extractURLs(final Page page) {
		List<WebURL> urlList = page.getLinksToFollow();
		ParseData data = page.getParseData();
		if (data instanceof HtmlParseData) {
			HtmlParseData htmlData = (HtmlParseData) data;
			// 1. 获取静态URL列表
			// urlList.addAll(htmlData.getOutgoingUrls());
			// 2. 获取动态URL列表
			String html = htmlData.getHtml();
			// 2.1 获取cmt_id的值
			String cmtId = this.getCmtId(html);
			// 2.2 构建需要爬去的动态URL的值
			// 评论数URL
			String cmtCountURLString = "http://coral.qq.com/article/" + cmtId
			        + "/commentnum";
			WebURL cmtCountURL = WebURL.newWebURL()
			                           .setURL(cmtCountURLString)
			                           .setShouldDepthIncrease(false)
			                           .setProperty("commentedDocID",
			                                        page.getWebURL()
			                                            .getDocid());
			// 第一条评论URL
			String firstCmtURLString = "http://coral.qq.com/article/" + cmtId
			        + "/comment?commentid=0";
			WebURL firstCmtURL = WebURL.newWebURL()
			                           .setURL(firstCmtURLString)
			                           .setShouldDepthIncrease(false)
			                           .setProperty("commentedDocID",
			                                        page.getWebURL()
			                                            .getDocid());
			// 2.3 将构建的动态URL添加到最终的列表中
			urlList.add(cmtCountURL);
			urlList.add(firstCmtURL);
			
		}
	}

	private String getCmtId(final String html) {
		Matcher cmtIdMatcher = this.cmtIdPattern.matcher(html);
		if (cmtIdMatcher.find()) {
			String cmtId = cmtIdMatcher.group(1);
			return cmtId;
		}
		return null;
	}
	
}
