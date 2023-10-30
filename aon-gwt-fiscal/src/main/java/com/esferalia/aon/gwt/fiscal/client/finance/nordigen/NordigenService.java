package com.esferalia.aon.gwt.fiscal.client.finance.nordigen;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/nordigen")
public interface NordigenService extends RemoteService {
	
	NordigenConfiguration getConfiguration(Occam occam) throws NordigenException;
	NordigenBankAccount setAccountValues(NordigenAccessToken token, Occam occam, NordigenBankAccount account) throws NordigenException;
	List<NordigenBankStatement> getNotInsertedMovements(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount) throws NordigenException;
	List<NordigenRequisition> findAllDomainRequisitions(NordigenAccessToken token, String currentDomainName) throws NordigenException;
	Boolean deleteRequisitionById (NordigenAccessToken token, Occam occam, String requisitionId) throws NordigenException;
	void cancelRequisition (NordigenAccessToken token, Occam occam, Integer rbankId) throws NordigenException;
	NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) throws NordigenException;
	List<NordigenBankStatement> getMovements(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount, Date endDate, boolean online) throws NordigenException; 
	NordigenRequisition addAccount(NordigenAccessToken token, Occam occam, NordigenBankAccount nordigenBankAccount) throws NordigenException;
	List<NordigenInstitution> getInstitutionsByBic(NordigenAccessToken token, String bic) throws NordigenException;
	Integer insertTransactions(Occam occam, NordigenBankAccount nordigenBankAccount) throws NordigenException;
	List<NordigenInstitution> getInstitutions(NordigenAccessToken token, Country country) throws NordigenException;
}
