package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
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
	public void getConfiguration(Occam occam, AsyncCallback<NordigenConfiguration> callback) {
		AON.start();
		fsa.getConfiguration(occam, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void setAccountValues(NordigenAccessToken token, Occam occam, NordigenBankAccount account, AsyncCallback<NordigenBankAccount> callback) {
		AON.start();
		fsa.setAccountValues(token, occam, account, callback);
	}

	@Override
	public void getDomainRequisitions(NordigenAccessToken token, String currentDomainName, AsyncCallback<List<NordigenRequisition>> callback) {
		AON.start();
		fsa.getDomainRequisitions(token, currentDomainName, callback);
	}

	@Override
	public void deleteRequisitionById(NordigenAccessToken token, Occam occam, String requisitionId, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.deleteRequisitionById(token, occam, requisitionId, callback);
		
	}
	
	@Override
	public void cancelRequisition(NordigenAccessToken token, Occam occam, Integer rbankId, AsyncCallback<Void> callback) {
		AON.start();
		fsa.cancelRequisition(token, occam, rbankId, callback);
	}
	
	@Override
	public void getRequisition(NordigenAccessToken token, String requisitionId, AsyncCallback<NordigenRequisition> callback) {
		AON.start();
		fsa.getRequisition(token, requisitionId, callback);
	}
	
	@Override
	public void getMovements(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online, AsyncCallback<List<NordigenBankStatement>> callback) {
		AON.start();
		fsa.getMovements(token, occam, nordigenBankAccount, endDate, online, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void addAccount(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount, AsyncCallback<NordigenRequisition> callback) {
		AON.start();
		fsa.addAccount(token, occam, nordigenBankAccount, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getInstitutionsByBic(NordigenAccessToken token, String bic, AsyncCallback<List<NordigenInstitution>> callback) {
		AON.start();
		fsa.getInstitutionsByBic(token, bic, callback);
	}

	@Override
	public void insertTransactions(Occam occam, NordigenBankAccount nordigenBankAccount, AsyncCallback<Integer> callback) {
		AON.start();
		fsa.insertTransactions(occam, nordigenBankAccount, new AsyncCallbackWrapper<>(callback));	
	}

	@Override
	public void getInstitutions(NordigenAccessToken token, Country country, AsyncCallback<List<NordigenInstitution>> callback) {
		AON.start();
		fsa.getInstitutions(token, country, callback);
	}

}
