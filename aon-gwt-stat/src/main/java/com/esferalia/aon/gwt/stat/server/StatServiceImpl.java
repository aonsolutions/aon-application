package com.esferalia.aon.gwt.stat.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Stats Servlet", urlPatterns = { "/aon_gwt_stat/Stats" 
													,"/aon_gwt_aio/Stats"})
public class StatServiceImpl extends AonStatelessRemoteServiceServlet implements StatService {

	@Override
	public StatParams createStatParams(String domainName,String user, int domain) throws AonCoreException {
		return AON.createStatParams(domainName,domain,user);
	}

	@Override
	public StatData<String, String, Double> getStatData(
			String domainName,String user,Integer domainId,StatParams params) throws AonCoreException {
		return AON.getStatData(domainName,domainId,user,params);
	}

	@Override
	public String getInvoicesReport(String domainName,String user, int domain, StatParams params) throws AonCoreException {
		return AON.getInvoicesReport(domainName,domain,user,params);
	}


}
