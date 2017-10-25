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
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalServiceAsync {
	// -------------------------------------------------------------- COMMON
	void mathExpression(String expression, AsyncCallback<Double> callback);
	
	// -------------------------------------------------------------- PARAMS
	void getFiscalParameters(String domainName,int domain,AsyncCallback<FiscalParameters> callback);

	// ------------------------------------------------------ FISCAL PANEL
	void getFiscalPanel(String currentDomainName,int currentDomain,int year,
			AsyncCallback<FiscalModelMatrix> asyncCallback);
	void getAllModels(String domainName, int domain, 
			AsyncCallback<LinkedList<IFiscalModel>> callback);
	void getAllModels(String domainName, int domain, int year,
			AsyncCallback<LinkedList<IFiscalModel>> callback);

	// -------------------------------------------------------------- ACTIVITIES
	void getActivities(int activityGroup,
			AsyncCallback<LinkedList<Activity>> callback);

	// ---------------------------------------------------------------MODELO 190
	void deleteMod190(String domainName, int domain,Mod190 mod190, AsyncCallback<Void> callback);
	void saveMod190(String domainName, int domain,Mod190 mod190, AsyncCallback<Mod190> callback);
	void getMod190s(String domainName, int domain, AsyncCallback<LinkedList<Mod190>> callback);
	void getMod190(String domainName, int domain,Integer id, AsyncCallback<Mod190> callback);
	void getMod190Detail(String domainName, int domain,Integer id, AsyncCallback<Mod190Detail> callback);
	void initializeMod190(String domainName, Integer domain, Integer year,AsyncCallback<Mod190> callback);

	// ---------------------------------------------------------------MODELO 184
	void deleteMod184(String domainName, int domain,Mod184 mod184, AsyncCallback<Void> callback);
	void saveMod184(String domainName, int domain,Mod184 mod184, AsyncCallback<Mod184> callback);
	void getMod184s(String domainName, int domain, AsyncCallback<LinkedList<Mod184>> callback);
	void getMod184(String domainName, int domain,Integer id, AsyncCallback<Mod184> callback);
	void initializeMod184(String domainName, Integer domain, Integer year,AsyncCallback<Mod184> callback);

	// ---------------------------------------------------------------MODELO 193
	void deleteMod193(String domainName, int domain,Mod193 mod193, AsyncCallback<Void> callback);
	void saveMod193(String domainName, int domain,Mod193 mod193, AsyncCallback<Mod193> callback);
	void getMod193s(String domainName, int domain, AsyncCallback<LinkedList<Mod193>> callback);
	void getMod193(String domainName, int domain,Integer id, AsyncCallback<Mod193> callback);
	void initializeMod193(String domainName, Integer domain, Integer year,AsyncCallback<Mod193> callback);

	// ---------------------------------------------------------------MODELO 390
	void getMod390s(String domainName, Integer domain, AsyncCallback<LinkedList<Mod390>> callback);
	
	// ---------------------------------------------------------------MODELO 390 - 2014
	void getMod3902014(String domainName, Integer domain,Integer id, AsyncCallback<Mod3902014> callback);
	void saveMod3902014(String domainName, Integer domain, Mod3902014 mod390, AsyncCallback<Mod3902014> callback);
	void deleteMod3902014(String domainName, Integer domain, Mod3902014 mod390, AsyncCallback<Void> callback);
	void initializeMod3902014(String domainName, Integer domain, Integer year, AsyncCallback<Mod3902014> callback);

	// ---------------------------------------------------------------MODELO 390 - 2015
	void getMod3902015(String domainName, Integer domain,Integer id, AsyncCallback<Mod3902015> callback);
	void saveMod3902015(String domainName, Integer domain, Mod3902015 mod390, AsyncCallback<Mod3902015> callback);
	void deleteMod3902015(String domainName, Integer domain, Mod3902015 mod390, AsyncCallback<Void> callback);
	void initializeMod3902015(String domainName, Integer domain, Integer year, AsyncCallback<Mod3902015> callback);
	
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
	void getAccountEntries(String domainName, int domain,
			AccountEntryParams params,int offset, int limit,
			AsyncCallback<LinkedList<AccountEntry>> callback);
	void getAccountEntry(String domainName, int domain, int id,
			AsyncCallback<AccountEntry> callback);
	void save(String domainName, int domain, AccountEntry ae,
			AsyncCallback<AccountEntry> callback);
	void insertSalaryAccountEntries(String domainName, int domain, Date from,
			Date to, String concept, Integer registryBank,
			AsyncCallback<LinkedList<AccountEntry>> callback);
	void previewSalaryAccountEntries(String domainName, int domain, Date from,
			Date to, String concept, Integer registryBank,
			AsyncCallback<LinkedList<AccountEntry>> callback);
	void getSalaryAccountEntries(String domainName, int domain,Date from, Date to,
			AsyncCallback<LinkedList<AccountEntry>> callback);
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

	// --------------------------------------------------------------- VAT
	void getVatSummaryContext(String domainName, int domain, VatParams params,
			AsyncCallback<LinkedList<VatSummaryContext>> callback);
	void getVatContext(String domainName, int domain, VatParams params, AsyncCallback<LinkedList<VatContext>> callback);
	void getVatContextReport(String domainName, int domain, VatParams params, AsyncCallback<String> callback);

	// --------------------------------------------------------------- ACCOUNT STATEMENT
	void getAccountStatement(String domainName, int domain, 
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

}
