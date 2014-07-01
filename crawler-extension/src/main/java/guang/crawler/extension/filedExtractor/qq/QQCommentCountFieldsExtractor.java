package guang.crawler.extension.filedExtractor.qq;

import guang.crawler.commons.DataFields;
import guang.crawler.commons.Page;
import guang.crawler.commons.parserData.HtmlParseData;
import guang.crawler.commons.parserData.ParseData;
import guang.crawler.connector.WebDataTableConnector;
import guang.crawler.extension.filedExtractor.FieldsExtractor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 处理类似于 <code>http://coral.qq.com/article/1009470758/commentnum</code>
 * URL的页面的信息。
 *
 * @author sun
 *
 */
public class QQCommentCountFieldsExtractor implements FieldsExtractor {
	
	@Override
	public void extractFields(final Page page) {
		ParseData parseData = page.getParseData();
		if (parseData instanceof HtmlParseData) {
			// 确保当前读到的是JSON数据
			HtmlParseData data = (HtmlParseData) parseData;
			String jsonString = data.getHtml();
			JSONObject responseObj = null;
			// 检查JSON字符串的合法性
			if ((jsonString == null)
			        || ((jsonString.length() == 0) || ((responseObj = JSON.parseObject(jsonString)) == null))
			        || ((responseObj = responseObj.getJSONObject("data")) == null)) {
				return;
			}
			String commentNum = responseObj.getString("commentnum");
			DataFields fields = page.getDataToSave();
			String commentedDocID = (String) page.getWebURL()
			                                     .getProperty("commentedDocID");
			if (commentedDocID != null) {
				fields.addFiled(commentedDocID,
				                WebDataTableConnector.FAMILY_SUPPORT_DATA,
				                "cmtCount", commentNum);
			}
		}
	}
}
