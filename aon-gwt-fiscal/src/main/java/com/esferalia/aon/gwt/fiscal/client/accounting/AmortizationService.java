package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.List;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Amortization")
public interface AmortizationService extends RemoteService {

	List<Account> getFixedAssetAccounts(String domainName, int domain, String user) throws AonCoreException;
	List<Account> getAccumulatedAccounts(String domainName, int domain, String user) throws AonCoreException;
	List<Account> getAllocationAccounts(String domainName, int domain, String user) throws AonCoreException;

	List<AmortizationType> getAmortizationTypeList(String domainName, int domain, String user, AmortizationTypeParams params) throws AonCoreException;
	void deleteAmortizationTypes(String domainName, int domain, String user, List<Integer> deleteIds) throws AonCoreException;
	void saveAmortizationType(String domainName, int domain, String user, AmortizationType amortizationType) throws AonCoreException;
	
}
