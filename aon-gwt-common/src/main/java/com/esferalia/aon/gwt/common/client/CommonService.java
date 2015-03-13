package com.esferalia.aon.gwt.common.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Common")
public interface CommonService extends RemoteService {

	// -------------------------------------------------------------- ENTERPRISE
	ArrayList<Enterprise> getParentEnterprises(String domainName, int domain,
			String query) throws AonSQLException;
	Enterprise getEnterprise(String domainName, int domain, int id) throws AonSQLException;

}
