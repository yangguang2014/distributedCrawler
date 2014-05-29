package guang.crawler.controller.webservice;

import guang.crawler.controller.SiteStatus;

import javax.jws.WebService;

import com.gzgb.epo.webservice.WebGatherNodeBean;

@WebService(
		name = "SiteManagerService",
		targetNamespace = "http://guang.crawler.controller.webservice/",
		portName = "SiteManagerService",
		serviceName = "SiteManagerService",
		endpointInterface = "guang.crawler.controller.webservice.SiteManagerService")
public class SiteManagerServiceImp implements SiteManagerService {

	@Override
	public boolean add(WebGatherNodeBean site) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Long siteID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean enable(Long siteID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SiteStatus status(Long siteID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean disable(Long siteID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(com.gzgb.epo.webservice.WebGatherNodeBean site) {
		// TODO Auto-generated method stub
		return false;
	}

}
