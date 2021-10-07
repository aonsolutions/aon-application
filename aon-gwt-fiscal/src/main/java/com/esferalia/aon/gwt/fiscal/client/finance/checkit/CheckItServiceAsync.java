package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import java.util.List;

import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLog;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CheckItServiceAsync {

	void getConfiguration(String currentDomainName, int currentDomain, String user, AsyncCallback<CheckItConfiguration> callback);

	void saveEnterpriseData(String currentDomainName, int currentDomain, String user,
			AsyncCallback<Integer> callback);
	void insertTransactions(String currentDomainName, int currentDomain, String user, Integer checkitEnterpriseId
			, CheckItBankAccount checkItBankAccount, AsyncCallback<Integer> callback);
	void getLogins(Integer bankId, AsyncCallback<List<CheckItLoginFields>> callback);
	void addAccount(Integer enterpriseId, CheckitUnlinkedBankAccount checkitUnlinkedBankAccount
			, String userID, String userPassword, String userPIN, AsyncCallback<Boolean> callback);
	void getCredentials(Integer enterpriseId, Integer loginId, AsyncCallback<CheckItLoginFields> callback);
	void editCredentials(Integer enterpriseId, CheckItLoginFields checkItLoginFields, AsyncCallback<Boolean> callback);
	void getFields(Integer loginId, AsyncCallback<CheckItLoginFields> callback);
}
