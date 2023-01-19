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
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/nordigen")
public interface NordigenService extends RemoteService {
	
	// --------------------------------------------------------------- RAWDOC
	//NORDIGEN
	NordigenConfiguration getConfiguration(String currentDomainName, int currentDomain, String user) throws Exception;
	NordigenRequisition addAccount(String currentDomainName, int currentDomain, String user, NordigenAccessToken token, NordigenBankAccount nordigenBankAccount) throws Exception;
	List<NordigenBankStatement> getMovements(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online) throws Exception; 
	List<NordigenInstitution> getNordigenInstitutions(NordigenAccessToken token, Country country) throws Exception;
	List<NordigenInstitution> getNordigenInstitutionsByBic(NordigenAccessToken token, String bic) throws Exception;
	Boolean cancelRequisition (NordigenAccessToken token, String currentDomainName, int currentDomain, String user, Integer rbankId) throws Exception;
	Boolean deleteRequisitionById (NordigenAccessToken token, String currentDomainName, int currentDomain, String user, String requisitionId) throws Exception;
	NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) throws Exception;
	Integer insertTransactions(String currentDomainName, int currentDomain, String user, NordigenBankAccount nordigenBankAccount) throws AonCoreException;
	List<NordigenRequisition> findAllDomainRequisitions(NordigenAccessToken token, String currentDomainName) throws Exception;
	
	NordigenBankAccount setNordigenAccountValues(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount account) throws Exception;
	List<NordigenBankStatement> getNotInsertedMovements(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount nordigenBankAccount) throws Exception;
	
	
}
