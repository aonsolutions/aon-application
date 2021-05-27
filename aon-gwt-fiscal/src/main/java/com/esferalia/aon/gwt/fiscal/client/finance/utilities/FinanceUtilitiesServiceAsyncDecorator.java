package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FinanceUtilitiesServiceAsyncDecorator implements FinanceUtilitiesServiceAsync {

	private FinanceUtilitiesServiceAsync fsa;

	public FinanceUtilitiesServiceAsyncDecorator(FinanceUtilitiesServiceAsync serviceAsync) throws AonCoreException{
		this.fsa = serviceAsync;
	}

	@Override
	public void getDomain(String domainName, String user, int domain, AsyncCallback<Domain> callback) throws AonCoreException{
		AON.start();
		fsa.getDomain(domainName, user, domain, new AsyncCallbackWrapper<Domain>(callback));
	}

	@Override
	public void getChildDomains(String domainName, String user, int domain,AsyncCallback<LinkedList<Domain>> callback) throws AonCoreException{
		AON.start();
		fsa.getChildDomains(domainName, user, domain, new AsyncCallbackWrapper<LinkedList<Domain>>(callback));
	}


	// Chequeo de facturas sin vencimientos
	@Override
	public void missingFinanceInvoices(String domainName, String user, Domain domain,
			FinanceUtilitiesParams params, 
			AsyncCallback<FinanceUtilitiesResult> callback) throws AonCoreException {
		AON.start();
		fsa.missingFinanceInvoices(domainName, user, domain, params, new AsyncCallbackWrapper<FinanceUtilitiesResult>(callback));
	}

	@Override
	public void missingFinanceInvoicesFix(String domainName, String user, Integer domain,Integer invoice, 
			AsyncCallback<Invoice> callback) throws AonCoreException {
		AON.start();
		fsa.missingFinanceInvoicesFix(domainName, user, domain, invoice, new AsyncCallbackWrapper<Invoice>(callback));
	}

	// Chequeo de integridad de vencimientos en facturas 
	@Override
	public void financeInvoiceIntegrity(String domainName, String user, Domain domain,
			AsyncCallback<FinanceUtilitiesResult> callback) throws AonCoreException {
		AON.start();
		fsa.financeInvoiceIntegrity(domainName, user, domain, new AsyncCallbackWrapper<FinanceUtilitiesResult>(callback));
	}
	@Override
	public void financeInvoiceIntegrityFix(String domainName, String user, Integer domain, Finance finance,AsyncCallback<Finance> callback) throws AonCoreException {
		AON.start();
		fsa.financeInvoiceIntegrityFix(domainName, user, domain, finance, new AsyncCallbackWrapper<Finance>(callback));
	}

}
