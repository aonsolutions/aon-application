package com.esferalia.aon.gwt.fiscal.client.finance.checkit;

import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/CheckIt")
public interface CheckItService extends RemoteService {
	
	// --------------------------------------------------------------- RAWDOC
	CheckItConfiguration getConfiguration(String currentDomainName, int currentDomain, String user) throws AonCoreException;

}
