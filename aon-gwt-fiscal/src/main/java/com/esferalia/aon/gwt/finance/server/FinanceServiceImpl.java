package com.esferalia.aon.gwt.finance.server;

import java.util.Date;
import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Finance Servlet", urlPatterns = { "/aon_gwt_fiscal/Finance" })
public class FinanceServiceImpl extends AonRemoteServiceServlet implements FinanceService {

	@Override
	public LinkedList<InvoiceSeries> getInvoiceSeries(String domainName, int domainId, Date from, Date to, boolean taxDate)
			throws AonCoreException {
		return AON.getInvoiceSeries(domainName, domainId, this.getUserLogin(), from, to, taxDate );
	}

	@Override
	public Integer getInvoiceNextNumber(String domainName, Integer domainId, Byte[] types, String series)
			throws AonCoreException {
		return AON.getInvoiceNextNumber(domainName, domainId, this.getUserLogin(), types, series);
	}
}
