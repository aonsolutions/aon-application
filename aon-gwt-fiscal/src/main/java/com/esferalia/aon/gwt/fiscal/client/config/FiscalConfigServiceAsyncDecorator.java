package com.esferalia.aon.gwt.fiscal.client.config;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FiscalConfigServiceAsyncDecorator implements FiscalConfigServiceAsync {

	private FiscalConfigServiceAsync fsa;

	public FiscalConfigServiceAsyncDecorator(FiscalConfigServiceAsync rawdocServiceAsync) {
		this.fsa = rawdocServiceAsync;
	}
	
	@Override
	public void getConfiguration(Occam occam, AsyncCallback<AonConfiguration> callback) {
		AON.start();
		fsa.getConfiguration(occam, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveParam(Occam occam, ApplicationParameter ap, AsyncCallback<ApplicationParameter> callback) {
		AON.start();
		fsa.saveParam(occam, ap, new AsyncCallbackWrapper<>(callback));
	}	
}
