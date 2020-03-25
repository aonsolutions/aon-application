package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Finance")
public interface FinanceService extends RemoteService {
	
	// --------------------------------------------------------------- INVOICE SERIES
	LinkedList<InvoiceSeries> getInvoiceSeries(String domainName, int domainId, String  user,Date from, Date to, boolean taxDate) throws AonCoreException;
	Integer getInvoiceNextNumber(String domainName, Integer domainId, String  user, Byte[] types, String series) throws AonCoreException;
	
	// --------------------------------------------------------------- FINANCE
	LinkedList<Finance> getFinancesForInvoice(String domainName, int domainId, String  user,Invoice invoice) throws AonCoreException;
	LinkedList<FinanceTracking> getFinanceTracking(String domainName, int domainId, String  user,Integer finance) throws AonCoreException;
	Finance settleFinance(String domainName, int domainId, String user, Integer finance)  throws AonCoreException;
	Finance undoFinance(String domainName, int domainId, String user, Integer finance) throws AonCoreException;
	Finance payFinance(String domainName, int domainId, String user, Finance finance) throws AonCoreException;

}
