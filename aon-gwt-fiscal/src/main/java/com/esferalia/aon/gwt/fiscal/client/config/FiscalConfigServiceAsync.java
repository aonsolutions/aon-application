package com.esferalia.aon.gwt.fiscal.client.config;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalConfigServiceAsync {

	// --------------------------------------------------------------- INVOICE SERIES
	void getConfiguration(Occam occam, AsyncCallback<AonConfiguration> callback);
	void saveParam(Occam occam, ApplicationParameter ap, AsyncCallback<ApplicationParameter> callback);

}
