package guang.crawler.extension.urlExtractor.qq;

import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.commons.parserData.ParseData;
import guang.crawler.commons.parserData.TextParseData;
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
		WebURL commentedURL = (WebURL) page.getWebURL()
		                                   .getProperty("commentedWebURL");
		if (commentedURL == null) {
			return;
		}
		ParseData parseData = page.getParseData();
		// 处理的必须是JSON数据
		if (parseData instanceof TextParseData) {
			// 必须是JSON数据
			TextParseData data = (TextParseData) parseData;
			String jsonString = data.getTextContent();
			if ((jsonString == null) || (jsonString.length() == 0)) {
				return;
			}
			JSONObject responseObject = JSON.parseObject(jsonString);
			if ((responseObject == null)
			        || ((responseObject = responseObject.getJSONObject("data")) == null)) {
				return;
			}
			String maxID = responseObject.getString("maxid");
			String lastID = responseObject.getString("last");
			String targetID = responseObject.getString("targetid");

			if ((targetID == null) || (maxID == null) || (lastID == null)
			        || maxID.equals(lastID)) {
				// 该新闻已经没有新的评论了
				return;
			}
			urlList.add(WebURL.newWebURL()
			                  .setURL("http://coral.qq.com/article/" + targetID
			                                  + "/comment?commentid=" + lastID)
			                  .setProperty("commentedWebURL", commentedURL));

		}

	}

}
