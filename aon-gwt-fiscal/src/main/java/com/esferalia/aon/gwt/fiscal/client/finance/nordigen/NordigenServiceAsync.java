package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NordigenServiceAsync {

	void getConfiguration(Occam occam, AsyncCallback<NordigenConfiguration> callback);
	void setAccountValues(NordigenAccessToken token, Occam occam, NordigenBankAccount account, boolean refresh, AsyncCallback<NordigenBankAccount> callback);
	void getDomainRequisitions(NordigenAccessToken token, String currentDomainName, AsyncCallback<List<NordigenRequisition>> callback);
	void deleteRequisitionById (NordigenAccessToken token, Occam occam, String requisitionId, AsyncCallback<Boolean> callback);
	void cancelRequisition (NordigenAccessToken token, Occam occam, Integer rbankId, AsyncCallback<Void> callback);
	void getRequisition(NordigenAccessToken token, String requisitionId, AsyncCallback<NordigenRequisition> callback);
	void getMovements(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online, AsyncCallback<List<NordigenBankStatement>> callback);
	void addAccount(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount, AsyncCallback<NordigenRequisition> callback);
	void getInstitutionsByBic(NordigenAccessToken token, String bic, AsyncCallback<List<NordigenInstitution>> callback);
	void insertTransactions(Occam occam, NordigenBankAccount nordigenBankAccount, AsyncCallback<Integer> callback);
	void getInstitutions(NordigenAccessToken token, Country country, AsyncCallback<List<NordigenInstitution>> callback);
	void setAllBankAccountValues(Occam occam, NordigenAccessToken token, AsyncCallback<List<NordigenBankAccount>> callback);
	void getRemainingDays(Occam occam,NordigenBankAccount account,AsyncCallback<Integer> callback);
	void getRemainingCallsToday(Occam occam, NordigenBankAccount account, AsyncCallback<Integer> callback);
	void getLatestRetryAfter(Occam occam, NordigenBankAccount account, AsyncCallback<String> callback);
	void getCallStatuses(Occam occam, NordigenBankAccount account, AsyncCallback<List<String>> callback);
    void getByRequisitionIsNotNull(Occam occam, AsyncCallback<List<RegistryBank>> callback);
    void getAccountIdByIban(NordigenAccessToken token, String requisitionId, RegistryBank rbank, AsyncCallback<String> callback);
    void checkIncorrectMovements(Occam occam, NordigenAccessToken token, List<String> accountIds, RegistryBank rbank, AsyncCallback<List<BankStatement>> callback);
}
