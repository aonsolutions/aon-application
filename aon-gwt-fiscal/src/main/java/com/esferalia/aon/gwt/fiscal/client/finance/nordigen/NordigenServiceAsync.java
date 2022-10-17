package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankStatement;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NordigenServiceAsync {

	void getConfiguration(String currentDomainName, int currentDomain, String user, AsyncCallback<CheckItConfiguration> callback);

	void saveEnterpriseData(String currentDomainName, int currentDomain, String user,
			AsyncCallback<Integer> callback);
	void insertTransactions(String currentDomainName, int currentDomain, String user, Integer checkitEnterpriseId
			, CheckItBankAccount checkItBankAccount, AsyncCallback<Integer> callback);
	void getLogins(Integer bankId, AsyncCallback<List<CheckItLoginFields>> callback);
	void addAccount(Integer enterpriseId, CheckitUnlinkedBankAccount checkitUnlinkedBankAccount
			, String userID, String userPassword, String userPIN, AsyncCallback<String> callback);
	void getCredentials(Integer enterpriseId, Integer loginId, AsyncCallback<CheckItLoginFields> callback);
	void editCredentials(Integer enterpriseId, CheckItLoginFields checkItLoginFields, AsyncCallback<Boolean> callback);
	void getFields(Integer loginId, AsyncCallback<CheckItLoginFields> callback);
	void getMovements(String domainName, int domain, String user, Integer empresaId, CheckItBankAccount checkItBankAccount, Date startDate, Date endDate, AsyncCallback<List<CheckItBankStatement>> callback); 
	void addExtraField(Integer enterpriseId, String iban, String extraField, AsyncCallback<Boolean> callback);
}
