package guang.crawler.crawlWorker;

import guang.crawler.commons.Page;
import guang.crawler.commons.parserData.HtmlParseData;
import guang.crawler.commons.parserData.ParseData;
import guang.crawler.crawlWorker.pageProcessor.DownloadPlugin;
import guang.crawler.crawlWorker.url.URLCanonicalizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class EchoCommentURLPlugin implements DownloadPlugin {

	private static String	viewURL			= "http://comment.ifeng.com/view.php";
	private static String	viewSpecialuRL	= "http://comment.ifeng.com/viewspecial.php";

	/**
	 * 在HTML页面中查找下面的内容: <code>
	 * var comment_json = {
		'docUrl':'http://news.ifeng.com/opinion/special/shenghuodabaozha/',
		'docName':'生活大爆炸遭禁播的背后',
		'skey':'190dd4',
		'pagesize':parseInt(0),
		'isSpecial':parseInt(1),
		'isMatch':parseInt(0),
		'cmtBox':parseInt(1),
		'banner':'',
		'sns':parseInt(1),
		'status':parseInt(1),
		'countIds':[],
		'links':[]
	   };
	 * </code> 然后用该内容组合成URL,访问该URL.
	 */
	@Override
	public boolean work(final Page page) {
		ParseData parseData = page.getParseData();
		if (parseData instanceof HtmlParseData) {
			HtmlParseData htmlData = (HtmlParseData) parseData;
			Pattern findPattern = Pattern
					.compile("var\\s+comment_json\\s*=\\s*(\\{[^}]+\\})\\s*;");
			Matcher findMatcher = findPattern.matcher(htmlData.getHtml());
			if (findMatcher.find()) {
				String jsonString = findMatcher.group(1).replaceAll(
						"parseInt\\(([.0-9]+)\\)", "$1");
				JSONObject result = JSON.parseObject(jsonString);
				StringBuilder urlBuilder = new StringBuilder();
				if (result.getInteger("isSpecial") == 1) {
					urlBuilder.append(EchoCommentURLPlugin.viewSpecialuRL);
				} else {
					urlBuilder.append(EchoCommentURLPlugin.viewURL);
				}
				urlBuilder.append("?doc_url=")
				.append(result.getString("docUrl"))
				.append("&doc_name=")
				.append(result.getString("docName")).append("&skey=")
						.append(result.getString("skey")).append("&p=1");
				String firstPageUrl = URLCanonicalizer
						.getCanonicalURL(urlBuilder.toString());
				System.out.println(firstPageUrl);
				return true;
			} else {
				System.out.println(htmlData.getHtml());
			}
		}
		return false;
	}

}
