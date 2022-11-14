package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NordigenServiceAsyncDecorator implements NordigenServiceAsync {

	private NordigenServiceAsync fsa;

	public NordigenServiceAsyncDecorator(NordigenServiceAsync rawdocServiceAsync) {
		this.fsa = rawdocServiceAsync;
	}
	
	@Override
	public void getConfiguration(String domainName, int domain, String user, AsyncCallback<NordigenConfiguration> callback) {
		AON.start();
		fsa.getConfiguration(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveEnterpriseData(String domainName, int domain, String user, AsyncCallback<Integer> callback) {
		AON.start();
		fsa.saveEnterpriseData(domainName, domain, user, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void insertTransactions(String currentDomainName, int currentDomain, String user, NordigenBankAccount nordigenBankAccount,
			AsyncCallback<Integer> callback) {
		AON.start();
		fsa.insertTransactions(currentDomainName, currentDomain, user, nordigenBankAccount, new AsyncCallbackWrapper<>(callback));	
	}

	@Override
	public void getLogins(Integer bankId, AsyncCallback<List<CheckItLoginFields>> callback) {
		AON.start();
		fsa.getLogins(bankId, new AsyncCallbackWrapper<>(callback));
		
	}

	@Override
	public void addAccount(String currentDomainName, int currentDomain, String user, NordigenAccessToken token, NordigenBankAccount nordigenBankAccount, AsyncCallback<NordigenRequisition> callback) {
		AON.start();
		fsa.addAccount(currentDomainName, currentDomain, user, token, nordigenBankAccount, new AsyncCallbackWrapper<>(callback));
		
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
	public void getMovements(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online, AsyncCallback<List<NordigenBankStatement>> callback) {
		AON.start();
		fsa.getMovements(token, domainName, domain, user, nordigenBankAccount, endDate, online, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void addExtraField(Integer enterpriseId, String iban, String extraField, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.addExtraField(enterpriseId, iban, extraField, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getNordigenInstitutions(NordigenAccessToken token, Country country, AsyncCallback<List<NordigenInstitution>> callback) {
		AON.start();
		fsa.getNordigenInstitutions(token, country, callback);
		
	}

	@Override
	public void getNordigenInstitutionsByBic(NordigenAccessToken token, String bic,
			AsyncCallback<List<NordigenInstitution>> callback) {
		AON.start();
		fsa.getNordigenInstitutionsByBic(token, bic, callback);
		
	}

	@Override
	public void clearIncompleteRequisitions(NordigenAccessToken token, String currentDomainName, int currentDomain, String user, AsyncCallback<Integer> callback) {
		AON.start();
		fsa.clearIncompleteRequisitions(token, currentDomainName, currentDomain, user, callback);
		
	}

	@Override
	public void cancelRequisition(NordigenAccessToken token, String currentDomainName, int currentDomain, String user
			, Integer rbankId, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.cancelRequisition(token, currentDomainName, currentDomain, user, rbankId, callback);
	}

	@Override
	public void getRequisition(NordigenAccessToken token, String requisitionId,
			AsyncCallback<NordigenRequisition> callback) {
		AON.start();
		fsa.getRequisition(token, requisitionId, callback);
	}

	@Override
	public void setNordigenAccountValues(NordigenAccessToken token, String domainName, int domain, String user,
			NordigenBankAccount account, AsyncCallback<NordigenBankAccount> callback) {
		AON.start();
		fsa.setNordigenAccountValues(token, domainName, domain, user, account, callback);
	}

	@Override
	public void getNotInsertedMovements(NordigenAccessToken token, String domainName, int domain, String user,
			NordigenBankAccount nordigenBankAccount, AsyncCallback<List<NordigenBankStatement>> callback) {
		AON.start();
		fsa.getNotInsertedMovements(token, domainName, domain, user, nordigenBankAccount, callback);
	}



}
