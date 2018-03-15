package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccountingUtilitiesServiceAsync {

	void getDomain(String domainName, String user, int domain, AsyncCallback<Domain> callback) throws AonCoreException;
	void getChildDomains(String domainName, String user, int domain, AsyncCallback<LinkedList<Domain>> callback) throws AonCoreException;
	
	// ParentLinker Module
	void checkParentLinker(String domainName, String user, Domain domain, Account account, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;
	void runParentLinker(String domainName, String user, Domain domain, Account account, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;

	// Apuntes sin lineas
	void emptyEntries(String domainName, String user, Domain domain, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;
	void unbalancedEntries(String domainName, String user, Domain domain, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;
	
}
