package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Finance")
public interface FinanceService extends RemoteService {
	
	// --------------------------------------------------------------- INVOICE SERIES
	LinkedList<InvoiceSeries> getInvoiceSeries(String domainName, int domainId, String  user,Date from, Date to, boolean taxDate) throws AonCoreException;
	Integer getInvoiceNextNumber(String domainName, Integer domainId, String  user, Byte[] types, String series) throws AonCoreException;
	
	// --------------------------------------------------------------- REGISTRY BANKS
	LinkedList<RegistryBank> getCompanyBanks(String domainName, int domainId, String  user) throws AonCoreException;
	LinkedList<RegistryBank> getRegistryBanks(String domainName, int domainId, String  user,Integer registry) throws AonCoreException;
	
	// --------------------------------------------------------------- FINANCE
	LinkedList<Finance> getFinances(String domainName, int domain, String  user, FinanceParams params, int offset,int limit) throws AonCoreException;
	LinkedList<Finance> getFinancesForInvoice(String domainName, int domainId, String  user,Invoice invoice) throws AonCoreException;
	LinkedList<FinanceTracking> getFinanceTracking(String domainName, int domainId, String  user,Integer finance) throws AonCoreException;
	Finance settleFinance(String domainName, int domainId, String user, Integer finance)  throws AonCoreException;
	Finance undoFinance(String domainName, int domainId, String user, Integer finance) throws AonCoreException;
	FinanceTracking payFinance(String domainName, int domainId, String user, FinanceTracking tracking) throws AonCoreException;
	FinanceTracking returnFinance(String domainName, int domainId, String user, FinanceTracking tracking) throws AonCoreException;
	
	LinkedList<Finance> getAccountFinances(String domainName, int domain, String user, FinanceParams params, int offset, int limit) throws AonCoreException;
	

}
