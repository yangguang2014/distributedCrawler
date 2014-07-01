package guang.crawler.extension.urlExtractor.qq;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.commons.parserData.HtmlParseData;
import guang.crawler.commons.parserData.ParseData;
import guang.crawler.extension.urlExtractor.URLsExtractor;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 从评论中获取下一条评论的地址
 *
 * @author sun
 *
 */
public class QQNewsCommentURLsExtractor implements URLsExtractor {

	@Override
	public void extractURLs(final Page page) {
		List<WebURL> urlList = page.getLinksToFollow();
		// 检测是否设置了被评论的页面
		String commentedDocID = (String) page.getWebURL()
		                                     .getProperty("commentedDocID");
		if (commentedDocID == null) {
			return;
		}
		ParseData parseData = page.getParseData();
		// 处理的必须是JSON数据
		if (parseData instanceof HtmlParseData) {
			// 必须是JSON数据
			HtmlParseData data = (HtmlParseData) parseData;
			String jsonString = data.getHtml();
			if ((jsonString == null) || (jsonString.length() == 0)) {
				return;
			}
			JSONObject responseObject = JSON.parseObject(jsonString);
			if ((responseObject == null)
			        || ((responseObject = responseObject.getJSONObject("data")) == null)) {
				return;
			}
			String lastID = responseObject.getString("last");
			String targetID = responseObject.getString("targetid");
			String retnum = responseObject.getString("retnum");
			int cmtCount = 0;
			try {
				cmtCount = Integer.parseInt(retnum);
			} catch (NumberFormatException e) {
				cmtCount = 0;
			}
			if (cmtCount == 0) {
				// 该新闻已经没有新的评论了
				return;
			}
			urlList.add(WebURL.newWebURL()
			                  .setURL("http://coral.qq.com/article/" + targetID
			                                  + "/comment?commentid=" + lastID)
			                  .setProperty("commentedDocID", commentedDocID)
			                  .setShouldDepthIncrease(false));

		}

	}

}
