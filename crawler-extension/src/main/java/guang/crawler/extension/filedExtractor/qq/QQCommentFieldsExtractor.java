package guang.crawler.extension.filedExtractor.qq;

import guang.crawler.commons.DataFields;
import guang.crawler.commons.Page;
import guang.crawler.commons.WebURL;
import guang.crawler.commons.parserData.ParseData;
import guang.crawler.commons.parserData.TextParseData;
import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.extension.filedExtractor.FieldsExtractor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 从QQ评论页面中抽取评论内容，处理类似
 * <code>http://coral.qq.com/article/1007777754/comment?commentid=5875472061804651770</code>
 * URL的页面。
 *
 * @author sun
 *
 */
public class QQCommentFieldsExtractor implements FieldsExtractor {
	
	@Override
	public void extractFields(final Page page) {
		ParseData parseData = page.getParseData();
		// 检测数据内容
		if (parseData instanceof TextParseData) {
			// 必须是JSON数据
			TextParseData data = (TextParseData) parseData;
			String jsonString = data.getTextContent();
			if ((jsonString == null) || (jsonString.length() == 0)) {
				return;
			}
			JSONObject responseObject = JSON.parseObject(jsonString);
			JSONArray jsonArray = null;
			if ((responseObject == null)
			        || ((responseObject = responseObject.getJSONObject("data")) == null)
			        || ((jsonArray = responseObject.getJSONArray("commentid")) == null)) {
				return;
			}
			// 处理每条评论
			DataFields dataFileds = page.getDataToSave();
			WebURL commentedURL = (WebURL) page.getWebURL()
			                                   .getProperty("commentedWebURL");
			int size = jsonArray.size();
			for (int i = 0; i < size; i++) {
				JSONObject comment = jsonArray.getJSONObject(i);
				String commentId = comment.getString("id");
				String commentData = comment.toJSONString();
				// 将评论内容添加到HBase中，每个评论一列。
				dataFileds.addFiled(commentedURL.getDocid(),
				                    WebDataTableConnector.FAMILY_SUPPORT_DATA,
				                    "cmt" + commentId, commentData);
			}
			
		}
	}
}
