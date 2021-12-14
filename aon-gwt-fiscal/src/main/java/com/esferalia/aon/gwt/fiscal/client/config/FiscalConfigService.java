package com.esferalia.aon.gwt.fiscal.client.config;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/FiscalConfig")
public interface FiscalConfigService extends RemoteService {

	AonConfiguration getConfiguration(Occam occam) throws AonCoreException;
	ApplicationParameter saveParam(Occam occam, ApplicationParameter ap) throws AonCoreException;

}
