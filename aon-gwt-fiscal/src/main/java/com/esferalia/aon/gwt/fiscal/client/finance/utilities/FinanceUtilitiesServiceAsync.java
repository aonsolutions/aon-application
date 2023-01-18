package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FinanceUtilitiesServiceAsync {

	void getDomain(Occam occam, AsyncCallback<Domain> callback) throws AonCoreException;
	void getChildDomains(Occam occam, AsyncCallback<LinkedList<Domain>> callback) throws AonCoreException;
	
	// Chequeo de facturas sin vencimientos
	void missingFinanceInvoices(Occam occam, Domain domain, FinanceUtilitiesParams params, AsyncCallback<FinanceUtilitiesResult> asyncCallback) throws AonCoreException;
	void missingFinanceInvoicesFix(Occam occam, Integer invoice, AsyncCallback<Invoice> asyncCallback) throws AonCoreException;

	// Chequeo de integridad de vencimientos en facturas 
	void financeInvoiceIntegrity(Occam occam, Domain domain,AsyncCallback<FinanceUtilitiesResult> callback) throws AonCoreException;
	void financeInvoiceIntegrityFix(Occam occam, Finance finance,AsyncCallback<Finance> callback) throws AonCoreException;
	
}
