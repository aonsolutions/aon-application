package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Fiscal")
public interface FiscalMSService extends RemoteService {

	// -------------------------------------------------------------- COMPANY
	LinkedList<CompanyBank> getCompanyBanks(String domainName, String user,int domain) throws AonCoreException;
	
	// -------------------------------------------------------------- ACCOUNT
	LinkedList<Creditor> getBasicCreditors(String domainName, String user,int domain,String query) throws AonCoreException;
	

}
