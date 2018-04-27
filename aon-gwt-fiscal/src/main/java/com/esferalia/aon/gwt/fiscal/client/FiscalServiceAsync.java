package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalServiceAsync {
	// -------------------------------------------------------------- COMMON
	void mathExpression(String expression, AsyncCallback<Double> callback);
	
	// -------------------------------------------------------------- PARAMS
	void getFiscalParameters(String domainName,int domain,AsyncCallback<FiscalParameters> callback);

	// -------------------------------------------------------------- ACTIVITIES
	void getActivities(int activityGroup,
			AsyncCallback<LinkedList<Activity>> callback);

	// ---------------------------------------------------------------MODELO 200 
	void getMod200s(String currentDomainName, int currentDomain, AsyncCallback<LinkedList<Mod200>> asyncCallback);

	// --------------------------------------------------------------- NORMALIZED MEMORY
	void readMemory(Memory memory, AsyncCallback<Memory> callback);
	void saveMemory(Memory memory, AsyncCallback<Memory> callback);
	void deleteMemory(Memory memory, AsyncCallback<Void> callback);

	// --------------------------------------------------------------- ACCOUNT PERIOD
	void getDomainPeriods(String domainName, int domain,
			AsyncCallback<LinkedList<AccountPeriod>> callback);

	// --------------------------------------------------------------- ACCOUNT ENTRIES
	void getAccountEntries(String domainName,String user, int domain,
			AccountEntryParams params,int offset, int limit,
			AsyncCallback<LinkedList<AccountEntry>> callback);
	void getAccountEntry(String domainName, int domain, int id,
			AsyncCallback<AccountEntry> callback);
	void save(String domainName, int domain, AccountEntry ae,
			AsyncCallback<AccountEntry> callback);
	void deleteAccountEntry(String domainName, int domain, Integer id,
			AsyncCallback<Void> callback);
	void initializeInvoice(String domainName, int domain,AccountingRegistry registry, Integer activity, Date issueDate,
			AsyncCallback<AccountingInvoice> callback);
	void getAccountingInvoice(String domainName, int domain, Integer accountEntry,
			AsyncCallback<AccountingInvoice> callback);
	void getAccountingInvoiceFromInvoice(String domainName, int domain, Integer invoiceId,
			AsyncCallback<AccountingInvoice> callback);
	void save(String currentDomainName, int currentDomain, AccountingInvoice invoice,
			AsyncCallback<AccountingInvoice> callback);
	void getRegistryLastAccountingInvoice(String currentDomainName, int currentDomain, Integer registryId,
			AsyncCallback<AccountingInvoice> asyncCallback);
	void rectifyInvoice(String currentDomainName, int currentDomain, Integer id, InvoiceRectificationData data,
			AsyncCallback<AccountingInvoice> asyncCallback);
	void getSalaryEntries(String domainName, int domain, Date from, Date to,
			AsyncCallback<LinkedList<SalaryEntry>> callback);
	void getSalaryFormatted(String domainName, int domain, Date from, Date to, AsyncCallback<String> callback);

	// --------------------------------------------------------------- ACCOUNT STATEMENT
	void getAccountStatement(String domainName,String user, int domain, 
			AccountStatementParams params,
			AsyncCallback<AccountStatementReport> callback);

	void getAccountBalance(String domainName, int domain, 
			AccountStatementParams params,
			AsyncCallback<LinkedList<AccountStatement>> callback);

	void getAccountFinances(String domainName, int domain,
			FinanceParams params,int offset, int limit,
			AsyncCallback<LinkedList<Finance>> callback);
	void getFinanceEntry(String domainName, int domain, Integer accountEntry,
			AsyncCallback<FinanceEntry> asyncCallback);
	void save(String currentDomainName, int currentDomain, FinanceEntry financeEntry,
			AsyncCallback<FinanceEntry> asyncCallback);

	// --------------------------------------------------------------- IRPF
	void getIrpfBreakdownSummary(String domainName, String user, int domain, IRPFParams params,
			AsyncCallback<LinkedList<IrpfBreakdown>> callback);
	void getIrpfBreakdown(String domainName, String user, int domain, IRPFParams params,
			AsyncCallback<LinkedList<IrpfBreakdown>> callback);
	
	// --------------------------------------------------------------- GWT API INFO

	void getAonData(String domainName, Integer domainId, AsyncCallback<AonData> callback);
	void presentationFile(String domainName, Integer domainId, String user, FiscalModelType type, Integer id, AsyncCallback<Integer> callback);
	void markAsFinished(String domainName, Integer domainId, String user, IFiscalModel model, AsyncCallback<Void> callback) throws AonCoreException;


}
