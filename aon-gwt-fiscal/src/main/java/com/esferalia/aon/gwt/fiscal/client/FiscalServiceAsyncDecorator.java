package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FiscalServiceAsyncDecorator implements FiscalServiceAsync {

	private FiscalServiceAsync fsa;

	public FiscalServiceAsyncDecorator(FiscalServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// -------------------------------------------------------------- COMMON
	@Override
	public void mathExpression(String expression, AsyncCallback<Double> callback) {
		AON.start();
		fsa.mathExpression(expression, new AsyncCallbackWrapper<Double>(callback));
	}

	// -------------------------------------------------------------- PARAMS
	@Override
	public void getFiscalParameters(String domainName, int domain, AsyncCallback<FiscalParameters> callback) {
		AON.start();
		fsa.getFiscalParameters(domainName, domain, new AsyncCallbackWrapper<FiscalParameters>(callback));
	}

	// -------------------------------------------------------------- ACTIVITIES
	@Override
	public void getActivities(int activityGroup, AsyncCallback<LinkedList<Activity>> callback) {
		AON.start();
		fsa.getActivities(activityGroup, new AsyncCallbackWrapper<LinkedList<Activity>>(callback));
	}

	// ---------------------------------------------------------------MODELO 200
	@Override
	public void getMod200s(String domainName, int domain, AsyncCallback<LinkedList<Mod200>> callback) {
		AON.start();
		fsa.getMod200s(domainName, domain, new AsyncCallbackWrapper<LinkedList<Mod200>>(callback));
	}

	// ---------------------------------------------------------------
	// NORMALIZED MEMORY
	@Override
	public void readMemory(Memory memory, AsyncCallback<Memory> callback) {
		AON.start();
		fsa.readMemory(memory, new AsyncCallbackWrapper<Memory>(callback));
	}

	@Override
	public void saveMemory(Memory memory, AsyncCallback<Memory> callback) {
		AON.start();
		fsa.saveMemory(memory, new AsyncCallbackWrapper<Memory>(callback));
	}

	@Override
	public void deleteMemory(Memory memory, AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMemory(memory, new AsyncCallbackWrapper<Void>(callback));
	}

	// --------------------------------------------------------------- GWT API INFO

	@Override
	public void getAonData(String domainName, Integer domainId, String user, AsyncCallback<AonData> callback) {

	}

	@Override
	public void getAonDataToken(String domainName, Integer domainId, String token, AsyncCallback<AonData> callback) {

	}

	@Override
	public void presentationFile(String domainName, Integer domainId, String user, FiscalModelType type, Integer id, AsyncCallback<Integer> callback) {

	}

	@Override
	public void markAsFinished(String domainName, Integer domainId, String user, IFiscalModel model, AsyncCallback<Void> callback) throws AonCoreException {

	}
}
