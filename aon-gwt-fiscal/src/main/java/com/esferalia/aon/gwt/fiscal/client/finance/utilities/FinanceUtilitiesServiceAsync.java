package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FinanceUtilitiesServiceAsync {

	void getDomain(String domainName, String user, int domain, AsyncCallback<Domain> callback) throws AonCoreException;
	void getChildDomains(String domainName, String user, int domain, AsyncCallback<LinkedList<Domain>> callback) throws AonCoreException;
	
	// Chequeo de facturas sin vencimientos
	void missingFinanceInvoices(String domainName, String user, Domain domain, FinanceUtilitiesParams params, AsyncCallback<FinanceUtilitiesResult> asyncCallback) throws AonCoreException;
	void missingFinanceInvoicesFix(String domainName, String user, Integer domain, Integer invoice, AsyncCallback<Invoice> asyncCallback) throws AonCoreException;

	// Chequeo de integridad de vencimientos en facturas 
	void financeInvoiceIntegrity(String domainName, String user, Domain domain,AsyncCallback<FinanceUtilitiesResult> callback) throws AonCoreException;
	void financeInvoiceIntegrityFix(String domainName, String user, Integer domain, Finance finance,AsyncCallback<Finance> callback) throws AonCoreException;
	
	
}
