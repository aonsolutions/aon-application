package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import java.util.List;

import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/CheckIt")
public interface CheckItService extends RemoteService {
	
	// --------------------------------------------------------------- RAWDOC
	CheckItConfiguration getConfiguration(String currentDomainName, int currentDomain, String user) throws AonCoreException;
	Integer saveEnterpriseData(String currentDomainName, int currentDomain, String user) throws AonCoreException;
	Integer insertTransactions(String currentDomainName, int currentDomain, String user, Integer checkitEnterpriseId, CheckItBankAccount checkItBankAccount) throws AonCoreException;
	List<CheckItLoginFields> getLogins(Integer bankId);
	Boolean addAccount(Integer enterpriseId, CheckitUnlinkedBankAccount checkitUnlinkedBankAccount, String userID, String userPassword, String userPIN) throws IllegalArgumentException;
	CheckItLoginFields getCredentials(Integer enterpriseId, Integer loginId) throws IllegalArgumentException;
	Boolean editCredentials(Integer enterpriseId, CheckItLoginFields checkItLoginFields) throws IllegalArgumentException;
	CheckItLoginFields getFields(Integer loginId) throws IllegalArgumentException;
}
