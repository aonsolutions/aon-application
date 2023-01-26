package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NordigenServiceAsync {

	void getConfiguration(String currentDomainName, int currentDomain, String user, AsyncCallback<NordigenConfiguration> callback);
	void insertTransactions(String currentDomainName, int currentDomain, String user, NordigenBankAccount nordigenBankAccount,
		AsyncCallback<Integer> callback);
	void addAccount(String currentDomainName, int currentDomain, String user, NordigenAccessToken token, NordigenBankAccount nordigenBankAccount, AsyncCallback<NordigenRequisition> callback);
	void getMovements(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online, AsyncCallback<List<NordigenBankStatement>> callback);
	void getNordigenInstitutions(NordigenAccessToken token, Country country, AsyncCallback<List<NordigenInstitution>> callback);
	void getNordigenInstitutionsByBic(NordigenAccessToken token, String bic, AsyncCallback<List<NordigenInstitution>> callback);
	void deleteRequisitionById (NordigenAccessToken token, String currentDomainName, int currentDomain, String user, String requisitionId, AsyncCallback<Boolean> callback);
	void cancelRequisition (NordigenAccessToken token, String currentDomainName, int currentDomain, String user, Integer rbankId, AsyncCallback<Boolean> callback);
	void getRequisition(NordigenAccessToken token, String requisitionId, AsyncCallback<NordigenRequisition> callback);
	void setNordigenAccountValues(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount account, AsyncCallback<NordigenBankAccount> callback);
	void getNotInsertedMovements(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount nordigenBankAccount, AsyncCallback<List<NordigenBankStatement>> callback);
	void findAllDomainRequisitions(NordigenAccessToken token, String currentDomainName, AsyncCallback<List<NordigenRequisition>> callback);
}
