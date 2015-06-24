package com.esferalia.aon.gwt.connect.client;

import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Connect")
public interface ConnectService extends RemoteService {

	void importZippedMod2002013(String domainName,int domain) throws AonCoreException;

}
