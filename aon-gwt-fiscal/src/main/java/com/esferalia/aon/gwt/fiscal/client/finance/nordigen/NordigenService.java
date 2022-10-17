package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankStatement;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/nordigen")
public interface NordigenService extends RemoteService {
	
	// --------------------------------------------------------------- RAWDOC
	CheckItConfiguration getConfiguration(String currentDomainName, int currentDomain, String user) throws AonCoreException;
	Integer saveEnterpriseData(String currentDomainName, int currentDomain, String user) throws AonCoreException;
	Integer insertTransactions(String currentDomainName, int currentDomain, String user, Integer checkitEnterpriseId, CheckItBankAccount checkItBankAccount) throws AonCoreException;
	List<CheckItLoginFields> getLogins(Integer bankId);
	String addAccount(Integer enterpriseId, CheckitUnlinkedBankAccount checkitUnlinkedBankAccount, String userID, String userPassword, String userPIN) throws IllegalArgumentException;
	CheckItLoginFields getCredentials(Integer enterpriseId, Integer loginId) throws IllegalArgumentException;
	Boolean editCredentials(Integer enterpriseId, CheckItLoginFields checkItLoginFields) throws IllegalArgumentException;
	CheckItLoginFields getFields(Integer loginId) throws IllegalArgumentException;
	List<CheckItBankStatement> getMovements(String domainName, int domain, String user, Integer empresaId, CheckItBankAccount checkItBankAccount, Date startDate, Date endDate) throws IllegalArgumentException; 
	Boolean addExtraField(Integer enterpriseId, String iban, String extraField);
}
