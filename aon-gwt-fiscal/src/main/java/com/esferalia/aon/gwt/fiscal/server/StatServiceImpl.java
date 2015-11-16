package com.esferalia.aon.gwt.fiscal.server;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.StatService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Stat Servlet", urlPatterns = { "/aon_gwt_fiscal/Stats" })
public class StatServiceImpl extends AonRemoteServiceServlet implements StatService {

	@Override
	public StatParams createStatParams(String domainName, int domain) throws AonCoreException {
		return AON.createStatParams(domainName,domain,this.getUserLogin());
	}

	@Override
	public StatData<Integer, InvoiceType, Double> getYearInvoiceTypeData(
			StatParams params) throws AonCoreException {
		return AON.getYearInvoiceTypeData(params,this.getUserLogin());
	}

	@Override
	public StatData<Integer, InvoiceType, Double> getMonthInvoiceTypeData(
			StatParams params) throws AonCoreException {
		return AON.getMonthInvoiceTypeData(params,this.getUserLogin());
	}


}
