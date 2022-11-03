package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_REQUISITION_STATUS;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/nordigen")
public interface NordigenService extends RemoteService {
	
	// --------------------------------------------------------------- RAWDOC
	Integer saveEnterpriseData(String currentDomainName, int currentDomain, String user) throws AonCoreException;
	Integer insertTransactions(String currentDomainName, int currentDomain, String user, Integer checkitEnterpriseId, CheckItBankAccount checkItBankAccount) throws AonCoreException;
	List<CheckItLoginFields> getLogins(Integer bankId);
	CheckItLoginFields getCredentials(Integer enterpriseId, Integer loginId) throws IllegalArgumentException;
	Boolean editCredentials(Integer enterpriseId, CheckItLoginFields checkItLoginFields) throws IllegalArgumentException;
	CheckItLoginFields getFields(Integer loginId) throws IllegalArgumentException;
	Boolean addExtraField(Integer enterpriseId, String iban, String extraField);
	
	//NORDIGEN
	NordigenConfiguration getConfiguration(String currentDomainName, int currentDomain, String user) throws Exception;
	NordigenRequisition addAccount(String currentDomainName, int currentDomain, String user, NordigenAccessToken token, NordigenBankAccount nordigenBankAccount) throws Exception;
	List<NordigenBankStatement> getMovements(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount nordigenBankAccount, Date endDate) throws Exception; 
	List<NordigenInstitution> getNordigenInstitutions(NordigenAccessToken token, Country country) throws Exception;
	List<NordigenInstitution> getNordigenInstitutionsByBic(NordigenAccessToken token, String bic) throws Exception;
	Integer clearIncompleteRequisitions(NordigenAccessToken token, String currentDomainName, int currentDomain, String user) throws Exception;
	Boolean cancelRequisition (NordigenAccessToken token, String currentDomainName, int currentDomain, String user, Integer rbankId) throws Exception;
	NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) throws Exception;
	
	NordigenBankAccount setNordigenAccountValues(NordigenAccessToken token, NordigenBankAccount account) throws Exception;
	
	
}
