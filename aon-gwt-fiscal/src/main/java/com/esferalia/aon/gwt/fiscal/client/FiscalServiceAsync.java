package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalServiceAsync {
	// -------------------------------------------------------------- COMMON
	void mathExpression(String expression, AsyncCallback<Double> callback);

	// -------------------------------------------------------------- PARAMS
	void getFiscalParameters(String domainName, int domain, AsyncCallback<FiscalParameters> callback);

	// -------------------------------------------------------------- ACTIVITIES
	void getActivities(int activityGroup, AsyncCallback<LinkedList<Activity>> callback);

	// ---------------------------------------------------------------MODELO 200
	void getMod200s(String currentDomainName, int currentDomain, AsyncCallback<LinkedList<Mod200>> asyncCallback);

	// --------------------------------------------------------------- NORMALIZED
	// MEMORY
	void readMemory(Memory memory, AsyncCallback<Memory> callback);

	void saveMemory(Memory memory, AsyncCallback<Memory> callback);

	void deleteMemory(Memory memory, AsyncCallback<Void> callback);

	// --------------------------------------------------------------- GWT API INFO

	void getAonData(String domainName, Integer domainId, String user, AsyncCallback<AonData> callback);

	void getAonDataToken(String domainName, Integer domainId, String token, AsyncCallback<AonData> callback);

	void presentationFile(String domainName, Integer domainId, String user, FiscalModelType type, Integer id, AsyncCallback<Integer> callback);

	void markAsFinished(String domainName, Integer domainId, String user, IFiscalModel model, AsyncCallback<Void> callback) throws AonCoreException;

}
