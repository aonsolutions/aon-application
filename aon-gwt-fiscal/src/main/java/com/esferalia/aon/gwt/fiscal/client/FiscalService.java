package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Fiscal")
public interface FiscalService extends RemoteService {
	// ---------------------------------- COMMON
	Double mathExpression(String expression) throws AonCoreException;

	// ------------------------------------------------------- FISCAL PARAMETERS
	FiscalParameters getFiscalParameters(String domainName, int domain) throws AonCoreException;

	// -------------------------------------------------------------- ACTIVITIES
	LinkedList<Activity> getActivities(int activityGroup) throws AonCoreException;

	// ---------------------------------------------------------------MODELO 200 
	LinkedList<Mod200> getMod200s(String currentDomainName, int currentDomain) throws AonCoreException;

	// --------------------------------------------------------------- NORMALIZED MEMORY
	Memory readMemory(Memory memory) throws AonCoreException;
	Memory saveMemory(Memory memory) throws AonCoreException;
	void deleteMemory(Memory memory) throws AonCoreException;
	
	// --------------------------------------------------------------- IRPF
	LinkedList<IrpfBreakdown> getIrpfBreakdownSummary(String domainName, String user, int domain, IRPFParams params) throws AonCoreException;
	LinkedList<IrpfBreakdown> getIrpfBreakdown(String domainName, String user, int domain, IRPFParams params) throws AonCoreException;

	// -------------------------------- ------------------------------- ACCOUNT PERIOD
	LinkedList<AccountPeriod> getDomainPeriods(String domainName,int domain) throws AonCoreException;

	// --------------------------------------------------------------- ACCOUNT ENTRIES
	LinkedList<AccountEntry> getAccountEntries(String domainName,String user,int domain, AccountEntryParams params,int offset, int limit) throws AonCoreException;
	AccountEntry getAccountEntry(String domainName,int domain, int id) throws AonCoreException;
	AccountEntry save(String domainName,int domain, AccountEntry ae) throws AonCoreException;
	void deleteAccountEntry(String domainName,int domain, Integer id) throws AonCoreException;
	AccountingInvoice getAccountingInvoice(String domainName, int domain, Integer accountEntry) throws AonCoreException;
	AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, Integer invoiceId) throws AonCoreException;
	LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(String domainName, int domain, String user, String query) throws AonCoreException;
	AccountingInvoice save(String currentDomainName, int currentDomain, AccountingInvoice invoice) throws AonCoreException;
	AccountingInvoice initializeInvoice(String domainName,int domain, AccountingRegistry registry, Integer activity, Date issueDate) throws AonCoreException; 
	AccountingInvoice getRegistryLastAccountingInvoice(String currentDomainName, int currentDomain, Integer registryId) throws AonCoreException;
	AccountingInvoice rectifyInvoice(String currentDomainName, int currentDomain, Integer id,
		InvoiceRectificationData data) throws AonCoreException;
	LinkedList<SalaryEntry> getSalaryEntries(String domainName, int domain, Date from, Date to) throws AonCoreException;
	String getSalaryFormatted(String domainName, int domain, Date from, Date to) throws AonCoreException;
	
	// --------------------------------------------------------------- ACCOUNT STATEMENT
	AccountStatementReport getAccountStatement(String domainName,String user,int domain, AccountingReportParams params) throws AonCoreException;	
	LinkedList<AccountStatement> getAccountBalance(String domainName,int domain, AccountingReportParams params) throws AonCoreException;
	// --------------------------------------------------------------- ACCOUNT OPERATING STATEMENT
	AccountOperatingReport getAccountOperatingReport(String domainName,String user,int domain, AccountingReportParams params) throws AonCoreException;	
	// --------------------------------------------------------------- ACCOUNT TRIAL BALANCE
	AccountTrialBalanceReport getAccountTrialBalanceReport(String domainName, String user, int domain, AccountingReportParams params) throws AonCoreException;
	// --------------------------------------------------------------- ACCOUNT BALANCE
	AccountBalanceReport getAccountBalanceReport(String domainName, String user, int domain, AccountingReportParams params) throws AonCoreException;


	LinkedList<Finance> getAccountFinances(String domainName, int domain, FinanceParams params, int offset,
			int limit) throws AonCoreException;
	FinanceEntry getFinanceEntry(String domainName, int domain, Integer accountEntry) throws AonCoreException;
	FinanceEntry save(String currentDomainName, int currentDomain, FinanceEntry financeEntry) throws AonCoreException;
	
	// --------------------------------------------------------------- GWT API INFO

	AonData getAonData(String domainName, Integer domainId, String user);
	AonData getAonDataToken(String domainName, Integer domainId, String token);

	Integer presentationFile(String domainName, Integer domainId, String user, FiscalModelType type, Integer id);

	void markAsFinished(String domainName, Integer domainId, String user, IFiscalModel model);


	
}
