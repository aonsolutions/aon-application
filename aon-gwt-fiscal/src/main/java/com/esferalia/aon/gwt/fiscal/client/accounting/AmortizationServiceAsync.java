package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.List;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AmortizationServiceAsync {

	void getFixedAssetAccounts(String domainName, int domain, String user, AsyncCallback<List<Account>> asyncCallback) throws AonCoreException;
	void getAccumulatedAccounts(String domainName, int domain, String user, AsyncCallback<List<Account>> asyncCallback) throws AonCoreException;
	void getAllocationAccounts(String domainName, int domain, String user, AsyncCallback<List<Account>> asyncCallback) throws AonCoreException;

	void getAmortizationTypeList(String domainName, int domain, String user, AmortizationTypeParams params,AsyncCallback<List<AmortizationType>> asyncCallback) throws AonCoreException;
	void deleteAmortizationTypes(String domainName, int domain, String user, List<Integer> deleteIds, AsyncCallback<Void> asyncCallback) throws AonCoreException;
	void saveAmortizationType(String domainName, int domain, String user, AmortizationType amortizationType, AsyncCallback<Void> asyncCallback) throws AonCoreException;

}
