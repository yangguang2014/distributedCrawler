package guang.crawler.crawlWorker.fetcher;

import guang.crawler.commons.Page;

import java.io.EOFException;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * 页面下载的结果
 *
 * @author sun
 *
 */
public class PageFetchResult {
	
	protected static final Logger	logger	      = Logger.getLogger(PageFetchResult.class);
	/**
	 * 请求服务器之后响应的返回码
	 */
	protected int	              statusCode;
	/**
	 * 得到的结果数据
	 */
	protected HttpEntity	      entity	      = null;
	/**
	 * 请求得到的响应的头信息
	 */
	protected Header[]	          responseHeaders	= null;
	/**
	 * 如果页面没有重定向，那么设置该域为当前爬取的页面
	 */
	protected String	          fetchedUrl	  = null;
	/**
	 * 如果页面被重定向了，那么就设置该域
	 */
	protected String	          movedToUrl	  = null;

	/**
	 * 如果当前entity不为空,那么就直接放弃
	 */
	public void discardContentIfNotConsumed() {
		try {
			if (this.entity != null) {
				EntityUtils.consume(this.entity);
			}
		} catch (EOFException e) {
			// We can ignore this exception. It can happen on compressed streams
			// which are not repeatable
		} catch (IOException e) {
			// We can ignore this exception. It can happen if the stream is
			// closed.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public HttpEntity getEntity() {
		return this.entity;
	}
	
	public String getFetchedUrl() {
		return this.fetchedUrl;
	}
	
	public String getMovedToUrl() {
		return this.movedToUrl;
	}
	
	public Header[] getResponseHeaders() {
		return this.responseHeaders;
	}
	
	public int getStatusCode() {
		return this.statusCode;
	}
	
	public void setEntity(final HttpEntity entity) {
		this.entity = entity;
	}
	
	public void setFetchedUrl(final String fetchedUrl) {
		this.fetchedUrl = fetchedUrl;
	}
	
	public void setMovedToUrl(final String movedToUrl) {
		this.movedToUrl = movedToUrl;
	}
	
	public void setResponseHeaders(final Header[] responseHeaders) {
		this.responseHeaders = responseHeaders;
	}
	
	public void setStatusCode(final int statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * 将当前对象转化为一个Page对象
	 * 
	 * @param page
	 * @return
	 */
	public boolean transformToPage(final Page page) {
		try {
			page.load(this.entity);
			page.setFetchResponseHeaders(this.responseHeaders);
			return true;
		} catch (Exception e) {
			PageFetchResult.logger.info("Exception while fetching content for: "
			        + page.getWebURL()
			              .getURL() + " [" + e.getMessage() + "]");
		}
		return false;
	}
	
}
