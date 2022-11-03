package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

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

public interface NordigenServiceAsync {

	void getConfiguration(String currentDomainName, int currentDomain, String user, AsyncCallback<NordigenConfiguration> callback);

	void saveEnterpriseData(String currentDomainName, int currentDomain, String user,
			AsyncCallback<Integer> callback);
	void insertTransactions(String currentDomainName, int currentDomain, String user, Integer checkitEnterpriseId
			, CheckItBankAccount checkItBankAccount, AsyncCallback<Integer> callback);
	void getLogins(Integer bankId, AsyncCallback<List<CheckItLoginFields>> callback);
	void addAccount(String currentDomainName, int currentDomain, String user, NordigenAccessToken token, NordigenBankAccount nordigenBankAccount, AsyncCallback<NordigenRequisition> callback);
	void getCredentials(Integer enterpriseId, Integer loginId, AsyncCallback<CheckItLoginFields> callback);
	void editCredentials(Integer enterpriseId, CheckItLoginFields checkItLoginFields, AsyncCallback<Boolean> callback);
	void getFields(Integer loginId, AsyncCallback<CheckItLoginFields> callback);
	void getMovements(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount nordigenBankAccount, Date endDate, AsyncCallback<List<NordigenBankStatement>> callback);
	void addExtraField(Integer enterpriseId, String iban, String extraField, AsyncCallback<Boolean> callback);
	void getNordigenInstitutions(NordigenAccessToken token, Country country, AsyncCallback<List<NordigenInstitution>> callback);
	void getNordigenInstitutionsByBic(NordigenAccessToken token, String bic, AsyncCallback<List<NordigenInstitution>> callback);
	void clearIncompleteRequisitions(NordigenAccessToken token, String currentDomainName, int currentDomain, String user, AsyncCallback<Integer> callback);
	void cancelRequisition (NordigenAccessToken token, String currentDomainName, int currentDomain, String user, Integer rbankId, AsyncCallback<Boolean> callback);
	void getRequisition(NordigenAccessToken token, String requisitionId, AsyncCallback<NordigenRequisition> callback);
	void setNordigenAccountValues(NordigenAccessToken token, NordigenBankAccount account, AsyncCallback<NordigenBankAccount> callback);
}
