package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankStatement;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLog;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NordigenServiceAsyncDecorator implements NordigenServiceAsync {

	private NordigenServiceAsync fsa;

	public NordigenServiceAsyncDecorator(NordigenServiceAsync rawdocServiceAsync) {
		this.fsa = rawdocServiceAsync;
	}
	
	@Override
	public void getConfiguration(String domainName, int domain, String user, AsyncCallback<CheckItConfiguration> callback) {
		AON.start();
		fsa.getConfiguration(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveEnterpriseData(String domainName, int domain, String user, AsyncCallback<Integer> callback) {
		AON.start();
		fsa.saveEnterpriseData(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void insertTransactions(String currentDomainName, int currentDomain, String user, Integer checkitEnterpriseId,
			CheckItBankAccount checkItBankAccount, AsyncCallback<Integer> callback) {
		AON.start();
		fsa.insertTransactions(currentDomainName, currentDomain, user, checkitEnterpriseId, checkItBankAccount, new AsyncCallbackWrapper<>(callback));	
	}

	@Override
	public void getLogins(Integer bankId, AsyncCallback<List<CheckItLoginFields>> callback) {
		AON.start();
		fsa.getLogins(bankId, new AsyncCallbackWrapper<>(callback));
		
	}

	@Override
	public void addAccount(Integer enterpriseId, CheckitUnlinkedBankAccount checkitUnlinkedBankAccount, String userID,
			String userPassword, String userPIN, AsyncCallback<String> callback) {
		AON.start();
		fsa.addAccount(enterpriseId, checkitUnlinkedBankAccount, userID, userPassword, userPIN, new AsyncCallbackWrapper<>(callback));
		
	}

	@Override
	public void getCredentials(Integer enterpriseId, Integer loginId, AsyncCallback<CheckItLoginFields> callback) {
		AON.start();
		fsa.getCredentials(enterpriseId, loginId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void editCredentials(Integer enterpriseId, CheckItLoginFields checkItLoginFields, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.editCredentials(enterpriseId, checkItLoginFields, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getFields(Integer loginId, AsyncCallback<CheckItLoginFields> callback) {
		AON.start();
		fsa.getFields(loginId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMovements(String domainName, int domain, String user, Integer empresaId, CheckItBankAccount checkItBankAccount, Date startDate,
			Date endDate, AsyncCallback<List<CheckItBankStatement>> callback) {
		AON.start();
		fsa.getMovements(domainName, domain, user, empresaId, checkItBankAccount, startDate, endDate, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void addExtraField(Integer enterpriseId, String iban, String extraField, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.addExtraField(enterpriseId, iban, extraField, new AsyncCallbackWrapper<>(callback));
	}
	
}
