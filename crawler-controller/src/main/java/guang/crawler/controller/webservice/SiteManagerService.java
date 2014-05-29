package guang.crawler.controller.webservice;

import guang.crawler.controller.SiteStatus;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.gzgb.epo.webservice.WebGatherNodeBean;

@WebService(targetNamespace = "http://guang.crawler.controller.webservice/")
public interface SiteManagerService {
	@WebMethod
	public boolean add(WebGatherNodeBean site);

	@WebMethod
	public boolean delete(Long siteID);

	@WebMethod
	public boolean start(Long siteID);

	@WebMethod
	public SiteStatus status(Long siteID);

	@WebMethod
	public boolean stop(Long siteID);

	@WebMethod
	public boolean update(com.gzgb.epo.webservice.WebGatherNodeBean site);
}
