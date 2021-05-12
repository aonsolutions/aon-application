package com.esferalia.aon.gwt.fiscal.client;


import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalMSServiceAsync {

	// ---------------------------------- COMPANY
	void getCompanyBanks(String domainName, String user, int domain, AsyncCallback<LinkedList<CompanyBank>> callback);
	// -------------------------------------------------------------- CREDITOR
	void getBasicCreditors(String domainName, String user, int domain, String query, AsyncCallback<LinkedList<Creditor>> callback);
	// -------------------------------------------------------------- ACTIVITIES
	void getActivities(int activityGroup, AsyncCallback<LinkedList<Activity>> callback);
	// --------------------------------------------------------------- GWT API INFO
	void getAonData(String domainName, Integer domainId, String user, AsyncCallback<AonData> callback);
	void getAonDataToken(String domainName, Integer domainId, String token, AsyncCallback<AonData> callback);
	void presentationFile(String domainName, Integer domainId, String user, FiscalModelType type, Integer id, AsyncCallback<Integer> callback);
	void markAsFinished(String domainName, Integer domainId, String user, IFiscalModel model, AsyncCallback<Void> callback) throws AonCoreException;

}
