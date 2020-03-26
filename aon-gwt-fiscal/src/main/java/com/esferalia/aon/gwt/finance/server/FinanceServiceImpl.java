package com.esferalia.aon.gwt.finance.server;

import java.util.Date;
import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Finance Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Finance" })
public class FinanceServiceImpl extends AonStatelessRemoteServiceServlet implements FinanceService {

	private static final long serialVersionUID = -6729210903627762279L;

	@Override
	public LinkedList<InvoiceSeries> getInvoiceSeries(String domainName, int domainId, String user, Date from, Date to, boolean taxDate)
			throws AonCoreException {
		return AON.getInvoiceSeries(domainName, domainId,user, from, to, taxDate );
	}

	@Override
	public Integer getInvoiceNextNumber(String domainName, Integer domainId, String user, Byte[] types, String series) throws AonCoreException {
		return AON.getInvoiceNextNumber(domainName, domainId,user, types, series);
	}
	
	@Override
	public LinkedList<FinanceTracking> getFinanceTracking(String domainName, int domainId, String user, Integer finance)
			throws AonCoreException {
		return AON.getFinanceTracking(domainName, domainId,user, finance);
	}

	@Override
	public LinkedList<Finance> getFinancesForInvoice(String domainName, int domainId, String user, Invoice invoice)
			throws AonCoreException {
		return AON.getFinancesForInvoice(domainName, domainId,user, invoice);
	}

	@Override
	public Finance settleFinance(String domainName, int domainId, String user, Integer finance) throws AonCoreException {
		return AON.settleFinance(domainName, domainId,user, finance);
	}

	@Override
	public Finance undoFinance(String domainName, int domainId, String user, Integer finance) throws AonCoreException {
		return AON.undoFinance(domainName, domainId,user, finance);
	}

	@Override
	public Finance payFinance(String domainName, int domainId, String user, Finance finance) throws AonCoreException {
		return AON.payFinance(domainName, domainId,user, finance);
	}

	@Override
	public LinkedList<RegistryBank> getCompanyBanks(String domainName, int domainId, String user) throws AonCoreException {
		try {
			return AON.getCompanyRegistryBanks(domainName, domainId,user);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public LinkedList<RegistryBank> getRegistryBanks(String domainName, int domainId, String user, Integer registry) throws AonCoreException {
		try {
			return AON.getRegistryBanks(domainName, domainId,user, registry);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
}
