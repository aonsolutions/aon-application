package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

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
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Fiscal")
public interface FiscalService extends RemoteService {
	// ---------------------------------- COMMON
	Double mathExpression(String expression) throws AonCoreException;

	// ------------------------------------------------------- FISCAL PARAMETERS
	FiscalParameters getFiscalParameters(String domainName, int domain) throws AonCoreException;

	// ------------------------------------------------------- FISCAL PANEL
	FiscalModelMatrix getFiscalPanel(String domainName,int domain, int y);
	LinkedList<IFiscalModel> getAllModels(String domainName, int domain);
	LinkedList<IFiscalModel> getAllModels(String domainName, int domain, int year);
	
	// -------------------------------------------------------------- ACTIVITIES
	LinkedList<Activity> getActivities(int activityGroup) throws AonCoreException;
	
	// ---------------------------------------------------------------MODELO 190
	void deleteMod190(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	Mod190 saveMod190(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	LinkedList<Mod190> getMod190s(String domainName, int domain) throws AonCoreException;
	Mod190 getMod190(String domainName, int domain,Integer id) throws AonCoreException;
	Mod190Detail getMod190Detail(String domainName, int domain,Integer id) throws AonCoreException;
	Mod190 initializeMod190(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 193
	void deleteMod193(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	Mod193 saveMod193(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	LinkedList<Mod193> getMod193s(String domainName, int domain) throws AonCoreException;
	Mod193 getMod193(String domainName, int domain,Integer id) throws AonCoreException;
	Mod193 initializeMod193(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 184
	void deleteMod184(String domainName, int domain,Mod184 mod184) throws AonCoreException;
	Mod184 saveMod184(String domainName, int domain,Mod184 mod184) throws AonCoreException;
	LinkedList<Mod184> getMod184s(String domainName, int domain) throws AonCoreException;
	Mod184 getMod184(String domainName, int domain,Integer id) throws AonCoreException;
	Mod184 initializeMod184(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 390
	LinkedList<Mod390> getMod390s(String domainName, Integer domain) throws AonCoreException;
	
	// ---------------------------------------------------------------MODELO 390 - 2014
	Mod3902014 getMod3902014(String domainName, Integer domain,Integer id) throws AonCoreException;
	Mod3902014 saveMod3902014(String domainName, Integer domain,Mod3902014 mod390) throws AonCoreException;
	void deleteMod3902014(String domainName, Integer domain,Mod3902014 mod390) throws AonCoreException;
	Mod3902014 initializeMod3902014(String domainName, Integer domain, Integer year);
	
	// ---------------------------------------------------------------MODELO 390 - 2015
	Mod3902015 getMod3902015(String domainName, Integer domain,Integer id) throws AonCoreException;
	Mod3902015 saveMod3902015(String domainName, Integer domain,Mod3902015 mod390) throws AonCoreException;
	void deleteMod3902015(String domainName, Integer domain,Mod3902015 mod390) throws AonCoreException;
	Mod3902015 initializeMod3902015(String domainName, Integer domain, Integer year);

	// ---------------------------------------------------------------MODELO 200 
	LinkedList<Mod200> getMod200s(String currentDomainName, int currentDomain) throws AonCoreException;

	// --------------------------------------------------------------- NORMALIZED MEMORY
	Memory readMemory(Memory memory) throws AonCoreException;
	Memory saveMemory(Memory memory) throws AonCoreException;
	void deleteMemory(Memory memory) throws AonCoreException;
	
	// -------------------------------- ------------------------------- ACCOUNT PERIOD
	LinkedList<AccountPeriod> getDomainPeriods(String domainName,int domain) throws AonCoreException;

	// --------------------------------------------------------------- ACCOUNT ENTRIES
	LinkedList<AccountEntry> getAccountEntries(String domainName,int domain
			, AccountEntryParams params,int offset, int limit) throws AonCoreException;
	AccountEntry getAccountEntry(String domainName,int domain, int id) throws AonCoreException;
	AccountEntry save(String domainName,int domain, AccountEntry ae) throws AonCoreException;
	LinkedList<AccountEntry> previewSalaryAccountEntries(String domainName,int domain, Date from, Date to,String concept,Integer registryBank) throws AonCoreException;
	LinkedList<AccountEntry> insertSalaryAccountEntries(String domainName,int domain, Date from, Date to,String concept,Integer registryBank) throws AonCoreException;
	LinkedList<AccountEntry> getSalaryAccountEntries(String domainName,int domain, Date from, Date to) throws AonCoreException;
	void deleteAccountEntry(String domainName,int domain, Integer id) throws AonCoreException;
	AccountingInvoice getAccountingInvoice(String domainName, int domain, Integer accountEntry) throws AonCoreException;
	AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, Integer invoiceId) throws AonCoreException;
	AccountingInvoice save(String currentDomainName, int currentDomain, AccountingInvoice invoice) throws AonCoreException;
	AccountingInvoice initializeInvoice(String domainName,int domain, AccountingRegistry registry, Integer activity, Date issueDate) throws AonCoreException; 
	AccountingInvoice getRegistryLastAccountingInvoice(String currentDomainName, int currentDomain, Integer registryId) throws AonCoreException;
	AccountingInvoice rectifyInvoice(String currentDomainName, int currentDomain, Integer id,
		InvoiceRectificationData data) throws AonCoreException;
	LinkedList<SalaryEntry> getSalaryEntries(String domainName, int domain, Date from, Date to) throws AonCoreException;
	String getSalaryFormatted(String domainName, int domain, Date from, Date to) throws AonCoreException;
	
	// --------------------------------------------------------------- VAT
	LinkedList<VatSummaryContext> getVatSummaryContext(String domainName, int domain,VatParams params) throws AonCoreException;
	LinkedList<VatContext> getVatContext(String domainName, int domain,VatParams params) throws AonCoreException;
	String getVatContextReport(String domainName, int domain,VatParams params) throws AonCoreException;
	
	
	// --------------------------------------------------------------- ACCOUNT STATEMENT
	AccountStatementReport getAccountStatement(String domainName,int domain, AccountStatementParams params) throws AonCoreException;	
	LinkedList<AccountStatement> getAccountBalance(String domainName,int domain, AccountStatementParams params) throws AonCoreException;


	LinkedList<Finance> getAccountFinances(String domainName, int domain, FinanceParams params, int offset,
			int limit) throws AonCoreException;
	FinanceEntry getFinanceEntry(String domainName, int domain, Integer accountEntry) throws AonCoreException;
	FinanceEntry save(String currentDomainName, int currentDomain, FinanceEntry financeEntry) throws AonCoreException;
	
}
