package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("roms/Tedi")
public interface TediService extends RemoteService {

	TediResult parseInvoice(String domainName, String user, int domain, String fileName, String content) throws AonCoreException;
	TediResult validateInvoice(String domainName, String user, int domain,TediResult result) throws AonCoreException;
	
}
