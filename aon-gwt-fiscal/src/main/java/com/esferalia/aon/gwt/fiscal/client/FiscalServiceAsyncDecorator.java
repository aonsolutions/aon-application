package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
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
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FiscalServiceAsyncDecorator implements FiscalServiceAsync {

	private FiscalServiceAsync fsa;

	public FiscalServiceAsyncDecorator(FiscalServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// -------------------------------------------------------------- COMMON
	@Override
	public void mathExpression(String expression, AsyncCallback<Double> callback) {
		AON.start();
		fsa.mathExpression(expression, new AsyncCallbackWrapper<Double>(
				callback));
	}

	// -------------------------------------------------------------- PARAMS
	@Override
	public void getFiscalParameters(String domainName, int domain,
			AsyncCallback<FiscalParameters> callback) {
		AON.start();
		fsa.getFiscalParameters(domainName, domain,
				new AsyncCallbackWrapper<FiscalParameters>(callback));
	}

	// ------------------------------------------------------- FISCAL PANEL
	@Override
	public void getFiscalPanel(String domainName, int domain, int year,
			AsyncCallback<FiscalModelMatrix> callback) {
		AON.start();
		fsa.getFiscalPanel(domainName, domain, year,
				new AsyncCallbackWrapper<FiscalModelMatrix>(callback));
	}

	@Override
	public void getAllModels(String domainName, int domain,
			AsyncCallback<LinkedList<IFiscalModel>> callback) {
		AON.start();
		fsa.getAllModels(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<IFiscalModel>>(callback));
	}

	@Override
	public void getAllModels(String domainName, int domain, int year,
			AsyncCallback<LinkedList<IFiscalModel>> callback) {
		AON.start();
		fsa.getAllModels(domainName, domain, year,
				new AsyncCallbackWrapper<LinkedList<IFiscalModel>>(callback));
	}

	// -------------------------------------------------------------- ACTIVITIES
	@Override
	public void getActivities(int activityGroup,
			AsyncCallback<LinkedList<Activity>> callback) {
		AON.start();
		fsa.getActivities(activityGroup,
				new AsyncCallbackWrapper<LinkedList<Activity>>(callback));
	}

	// ---------------------------------------------------------------MODELO 200
	@Override
	public void getMod200s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod200>> callback) {
		AON.start();
		fsa.getMod200s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod200>>(callback));
	}

	// ---------------------------------------------------------------
	// NORMALIZED MEMORY
	@Override
	public void readMemory(Memory memory, AsyncCallback<Memory> callback) {
		AON.start();
		fsa.readMemory(memory, new AsyncCallbackWrapper<Memory>(callback));
	}

	@Override
	public void saveMemory(Memory memory, AsyncCallback<Memory> callback) {
		AON.start();
		fsa.saveMemory(memory, new AsyncCallbackWrapper<Memory>(callback));
	}

	@Override
	public void deleteMemory(Memory memory, AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMemory(memory, new AsyncCallbackWrapper<Void>(callback));
	}

	// --------------------------------------------------------------- ACCOUNT
	// PERIOD
	@Override
	public void getDomainPeriods(String domainName, int domain,
			AsyncCallback<LinkedList<AccountPeriod>> callback) {
		AON.start();
		fsa.getDomainPeriods(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<AccountPeriod>>(callback));
	}

	// --------------------------------------------------------------- ACCOUNT
	// ENTRIES
	@Override
	public void getAccountEntries(String domainName, int domain,
			AccountEntryParams params, int offset, int limit,
			AsyncCallback<LinkedList<AccountEntry>> callback) {
		AON.start();
		fsa.getAccountEntries(domainName, domain, params, offset, limit,
				new AsyncCallbackWrapper<LinkedList<AccountEntry>>(callback));
	}

	@Override
	public void getAccountEntry(String domainName, int domain, int id,
			AsyncCallback<AccountEntry> callback) {
		AON.start();
		fsa.getAccountEntry(domainName, domain, id,
				new AsyncCallbackWrapper<AccountEntry>(callback));
	}

	@Override
	public void save(String domainName, int domain, AccountEntry ae,
			AsyncCallback<AccountEntry> callback) {
		AON.start();
		fsa.save(domainName, domain, ae,
				new AsyncCallbackWrapper<AccountEntry>(callback));
	}

	@Override
	public void deleteAccountEntry(String domainName, int domain, Integer id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteAccountEntry(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void initializeInvoice(String domainName, int domain,AccountingRegistry registry
		,Integer activity,Date issueDate,AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.initializeInvoice(domainName, domain, registry,activity,issueDate,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}
	
	@Override
	public void getAccountingInvoice(String domainName, int domain, Integer accountEntry,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getAccountingInvoice(domainName, domain, accountEntry,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}
	
	@Override
	public void getAccountingInvoiceFromInvoice(String domainName, int domain, Integer invoiceId,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getAccountingInvoiceFromInvoice(domainName, domain, invoiceId,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}
	

	@Override
	public void save(String domainName, int domain, AccountingInvoice invoice,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.save(domainName, domain, invoice,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}

	@Override
	public void getRegistryLastAccountingInvoice(String currentDomainName, int currentDomain, Integer registryId,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getRegistryLastAccountingInvoice(currentDomainName,currentDomain, registryId,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}

	@Override
	public void rectifyInvoice(String currentDomainName, int currentDomain, Integer invoiceId, InvoiceRectificationData data,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.rectifyInvoice(currentDomainName,currentDomain, invoiceId, data,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}

	@Override
	public void getSalaryEntries(String domainName, int domain, Date from, Date to,
			AsyncCallback<LinkedList<SalaryEntry>> callback) {
		AON.start();
		fsa.getSalaryEntries(domainName,domain, from, to,
				new AsyncCallbackWrapper<LinkedList<SalaryEntry>>(callback));
	}
	
	@Override
	public void getSalaryFormatted(String domainName, int domain, Date from, Date to, AsyncCallback<String> callback) {
		AON.start();
		fsa.getSalaryFormatted(domainName,domain, from, to,
				new AsyncCallbackWrapper<String>(callback));
	}

	// --------------------------------------------------------------- ACCOUNT
	// STATEMENT
	@Override
	public void getAccountStatement(String domainName, int domain,
			AccountStatementParams params,
			AsyncCallback<AccountStatementReport> callback) {
		AON.start();
		fsa.getAccountStatement(domainName, domain, params,
				new AsyncCallbackWrapper<AccountStatementReport>(callback));
	}

	@Override
	public void getAccountBalance(String domainName, int domain,
			AccountStatementParams params,
			AsyncCallback<LinkedList<AccountStatement>> callback) {
		AON.start();
		fsa.getAccountBalance(
				domainName,
				domain,
				params,
				new AsyncCallbackWrapper<LinkedList<AccountStatement>>(callback));
	}

	@Override
	public void getAccountFinances(String domainName, int domain, FinanceParams params, int offset, int limit,
			AsyncCallback<LinkedList<Finance>> callback) {
		AON.start();
		fsa.getAccountFinances(domainName, domain, params, offset, limit,
				new AsyncCallbackWrapper<LinkedList<Finance>>(callback));
	}

	@Override
	public void getFinanceEntry(String domainName, int domain, Integer accountEntry,
			AsyncCallback<FinanceEntry> callback) {
		AON.start();
		fsa.getFinanceEntry(domainName, domain, accountEntry,
				new AsyncCallbackWrapper<FinanceEntry>(callback));
	}

	@Override
	public void save(String domainName, int domain, FinanceEntry financeEntry,
			AsyncCallback<FinanceEntry> asyncCallback) {
		AON.start();
		fsa.save(domainName, domain, financeEntry,
				new AsyncCallbackWrapper<FinanceEntry>(asyncCallback));
	}

	// --------------------------------------------------------------- IRPF
	@Override
	public void getIrpfBreakdownSummary(String domainName, String user, int domain, IRPFParams params,
			AsyncCallback<LinkedList<IrpfBreakdown>> callback) {
		AON.start();
		fsa.getIrpfBreakdownSummary(domainName, user, domain, params,
				new AsyncCallbackWrapper<LinkedList<IrpfBreakdown>>(callback));
	}

	@Override
	public void getIrpfBreakdown(String domainName, String user, int domain, IRPFParams params,
			AsyncCallback<LinkedList<IrpfBreakdown>> callback) {
		AON.start();
		fsa.getIrpfBreakdown(domainName, user, domain, params,
				new AsyncCallbackWrapper<LinkedList<IrpfBreakdown>>(callback));
	}

	// --------------------------------------------------------------- GWT API INFO
	
	@Override
	public void getAonData(String domainName, Integer domainId, AsyncCallback<AonData> callback) {
		
	}



}
