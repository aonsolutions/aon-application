package com.esferalia.aon.gwt.common.client;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Common")
public interface CommonMSService extends RemoteService {

	// --------------------------------------------------------- CONFIGURATION
	AonConfiguration getAonConfiguration(String domainName, String user, int domain) throws AonCoreException;
}
