package com.esferalia.aon.gwt.fiscal.client;


import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalMSServiceAsync {

	// ---------------------------------- COMPANY
	void getCompanyBanks(String domainName, String user, int domain, AsyncCallback<LinkedList<CompanyBank>> callback);

	// -------------------------------------------------------------- CREDITOR
	void getBasicCreditors(String domainName, String user, int domain, String query, AsyncCallback<LinkedList<Creditor>> callback);

}
