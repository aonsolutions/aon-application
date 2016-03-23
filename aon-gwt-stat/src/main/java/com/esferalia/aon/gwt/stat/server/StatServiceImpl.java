package com.esferalia.aon.gwt.stat.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Stats Servlet", urlPatterns = { "/aon_gwt_stat/Stats" })
public class StatServiceImpl extends AonRemoteServiceServlet implements StatService {

	@Override
	public StatParams createStatParams(String domainName, int domain) throws AonCoreException {
		return AON.createStatParams(domainName,domain,this.getUserLogin());
	}

	@Override
	public StatData<Integer, String, Double> getYearInvoiceTypeData(
			String domainName,Integer domainId,StatParams params) throws AonCoreException {
		return AON.getYearInvoiceTypeData(domainName,domainId,this.getUserLogin(),params);
	}

	@Override
	public StatData<Integer, String, Double> getMonthInvoiceTypeData(
			String domainName,Integer domainId,StatParams params) throws AonCoreException {
		return AON.getMonthInvoiceTypeData(domainName,domainId,this.getUserLogin(),params);
	}
	
	@Override
	public StatData<Integer, String, Double> getDayInvoiceTypeData(
			String domainName,Integer domainId,StatParams params) throws AonCoreException {
		return AON.getDayInvoiceTypeData(domainName,domainId,this.getUserLogin(),params);
	}


}
