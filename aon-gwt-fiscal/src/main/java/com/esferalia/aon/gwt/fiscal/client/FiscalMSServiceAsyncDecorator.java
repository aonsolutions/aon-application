package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class FiscalMSServiceAsyncDecorator implements FiscalMSServiceAsync {

	private FiscalMSServiceAsync serviceAsync;

	public FiscalMSServiceAsyncDecorator(FiscalMSServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// -------------------------------------------------------------- PARAMS
	@Override
	public void getCompanyBanks(String domainName, String user, int domain,
			AsyncCallback<LinkedList<CompanyBank>> callback){
		AON.start();
		serviceAsync.getCompanyBanks(domainName,user, domain, new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}

	// -------------------------------------------------------------- CREDITOR
	@Override
	public void getBasicCreditors(Occam occam, String query,
			AsyncCallback<LinkedList<Creditor>> callback) {
		AON.start();
		serviceAsync.getBasicCreditors(occam, query, new AsyncCallbackWrapper<LinkedList<Creditor>>(callback));
	}
}
