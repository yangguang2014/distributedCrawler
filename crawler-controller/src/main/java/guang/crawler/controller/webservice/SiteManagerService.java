package guang.crawler.controller.webservice;

import guang.crawler.controller.SiteStatus;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.gzgb.epo.webservice.WebGatherNodeBean;

@WebService(targetNamespace = "http://guang.crawler.controller.webservice/")
public interface SiteManagerService {
	/**
	 * 增加一个采集点
	 * 
	 * @param site
	 * @return
	 */
	@WebMethod
	public boolean add(WebGatherNodeBean site);

	/**
	 * 删除某个采集点
	 * 
	 * @param siteID
	 * @return
	 */
	@WebMethod
	public boolean delete(Long siteID);

	/**
	 * 启动某个采集点
	 * 
	 * @param siteID
	 * @return
	 */
	@WebMethod
	public boolean enable(Long siteID);

	/**
	 * 获取某个采集点的信息
	 * 
	 * @param siteID
	 * @return
	 */
	@WebMethod
	public SiteStatus status(Long siteID);

	/**
	 * 停止某个采集点
	 * 
	 * @param siteID
	 * @return
	 */
	@WebMethod
	public boolean disable(Long siteID);

	/**
	 * 更新某个采集点中的配置信息
	 * 
	 * @param site
	 * @return
	 */
	@WebMethod
	public boolean update(com.gzgb.epo.webservice.WebGatherNodeBean site);
}
