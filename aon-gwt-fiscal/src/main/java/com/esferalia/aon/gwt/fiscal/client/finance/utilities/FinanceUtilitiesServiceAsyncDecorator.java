package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
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
	public void getDomain(Occam occam, AsyncCallback<Domain> callback) throws AonCoreException{
		AON.start();
		fsa.getDomain(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getChildDomains(Occam occam,AsyncCallback<LinkedList<Domain>> callback) throws AonCoreException{
		AON.start();
		fsa.getChildDomains(occam , new AsyncCallbackWrapper<>(callback));
	}


	// Chequeo de facturas sin vencimientos
	@Override
	public void missingFinanceInvoices(Occam occam, Domain domain, FinanceUtilitiesParams params, AsyncCallback<FinanceUtilitiesResult> callback) throws AonCoreException {
		AON.start();
		fsa.missingFinanceInvoices(occam, domain, params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void missingFinanceInvoicesFix(Occam occam, Integer invoice, AsyncCallback<Invoice> callback) throws AonCoreException {
		AON.start();
		fsa.missingFinanceInvoicesFix(occam, invoice, new AsyncCallbackWrapper<>(callback));
	}

	// Chequeo de integridad de vencimientos en facturas 
	@Override
	public void financeInvoiceIntegrity(Occam occam, Domain domain, AsyncCallback<FinanceUtilitiesResult> callback) throws AonCoreException {
		AON.start();
		fsa.financeInvoiceIntegrity(occam, domain, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void financeInvoiceIntegrityFix(Occam occam, Finance finance,AsyncCallback<Finance> callback) throws AonCoreException {
		AON.start();
		fsa.financeInvoiceIntegrityFix(occam, finance, new AsyncCallbackWrapper<>(callback));
	}
	

}
