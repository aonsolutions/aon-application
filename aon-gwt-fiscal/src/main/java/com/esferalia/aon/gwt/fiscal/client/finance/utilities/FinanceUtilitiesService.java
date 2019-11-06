package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/FinanceUtilities")
public interface FinanceUtilitiesService extends RemoteService {
	
	Domain getDomain(String domainName,String user, int domain) throws AonCoreException;
	LinkedList<Domain> getChildDomains(String domainName,String user, int domain) throws AonCoreException;

	// Chequeo de facturas sin vencimientos
	FinanceUtilitiesResult missingFinanceInvoices(String domainName, String user, Domain domain, FinanceUtilitiesParams params) throws AonCoreException;
	Invoice missingFinanceInvoicesFix(String domainName, String user, Integer domain, Integer invoice) throws AonCoreException;

}
