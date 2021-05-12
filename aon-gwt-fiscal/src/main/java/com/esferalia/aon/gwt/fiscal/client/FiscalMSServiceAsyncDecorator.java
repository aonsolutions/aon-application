package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.watson.error.AonCoreException;
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
	public void getBasicCreditors(String domainName, String user, int domain, String query,
			AsyncCallback<LinkedList<Creditor>> callback) {
		AON.start();
		serviceAsync.getBasicCreditors(domainName,user, domain, query, new AsyncCallbackWrapper<LinkedList<Creditor>>(callback));
	}
	// -------------------------------------------------------------- ACTIVITIES
	@Override
	public void getActivities(int activityGroup, AsyncCallback<LinkedList<Activity>> callback) {
		AON.start();
		serviceAsync.getActivities(activityGroup, new AsyncCallbackWrapper<LinkedList<Activity>>(callback));
	}
	// --------------------------------------------------------------- GWT API INFO
	@Override
	public void getAonData(String domainName, Integer domainId, String user, AsyncCallback<AonData> callback) {
		AON.start();
		serviceAsync.getAonData(domainName,domainId, user, new AsyncCallbackWrapper<AonData>(callback));
	}

	@Override
	public void getAonDataToken(String domainName, Integer domainId, String token, AsyncCallback<AonData> callback) {
		AON.start();
		serviceAsync.getAonDataToken(domainName,domainId, token, new AsyncCallbackWrapper<AonData>(callback));
	}

	@Override
	public void presentationFile(String domainName, Integer domainId, String user, FiscalModelType type, Integer id, AsyncCallback<Integer> callback) {
		AON.start();
		serviceAsync.presentationFile(domainName,domainId, user, type, id, new AsyncCallbackWrapper<Integer>(callback));
	}

	@Override
	public void markAsFinished(String domainName, Integer domainId, String user, IFiscalModel model, AsyncCallback<Void> callback) throws AonCoreException {
		AON.start();
		serviceAsync.markAsFinished(domainName,domainId, user, model, new AsyncCallbackWrapper<Void>(callback));
	}
}
