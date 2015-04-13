package com.esferalia.aon.gwt.common.client;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.rpc.AsyncCallback;



public interface CommonServiceAsync {

	// ---------------------------------- ENTERPRISE
	void getParentEnterprises(String domainName, int domain
			,String query,AsyncCallback<ArrayList<Enterprise>> callback);
	void getEnterprise(String domainName, int domain, int id
			,AsyncCallback<Enterprise> callback);
	
	void getCompanyBanks(String domainName,int domain,int enterprise
			,AsyncCallback<ArrayList<CompanyBank>> callback
			);
	void getCompanyBanks(String domainName,int domain
			,AsyncCallback<ArrayList<CompanyBank>> callback
			);
}
