package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AmortizationServiceAsyncDecorator implements AmortizationServiceAsync {

	private AmortizationServiceAsync fsa;

	public AmortizationServiceAsyncDecorator(AmortizationServiceAsync serviceAsync) {
		this.fsa = serviceAsync;
	}

	@Override
	public void getFixedAssetAccounts(String domainName, int domain, String user, AsyncCallback<List<Account>> callback) throws AonCoreException {
		AON.start();
		fsa.getFixedAssetAccounts(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAccumulatedAccounts(String domainName, int domain, String user, AsyncCallback<List<Account>> callback) throws AonCoreException {
		AON.start();
		fsa.getAccumulatedAccounts(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAllocationAccounts(String domainName, int domain, String user, AsyncCallback<List<Account>> callback) throws AonCoreException {
		AON.start();
		fsa.getAllocationAccounts(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAmortizationTypeList(String domainName, int domain, String user, AmortizationTypeParams params, AsyncCallback<List<AmortizationType>> callback) throws AonCoreException {
		AON.start();
		fsa.getAmortizationTypeList(domainName, domain, user, params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteAmortizationTypes(String domainName, int domain, String user, List<Integer> deleteIds, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		fsa.deleteAmortizationTypes(domainName, domain, user, deleteIds, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveAmortizationType(String domainName, int domain, String user, AmortizationType amortizationType, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		fsa.saveAmortizationType(domainName, domain, user, amortizationType, new AsyncCallbackWrapper<>(callback));
	}
	
}
