package com.esferalia.aon.gwt.common.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Common")
public interface CommonService extends RemoteService {

	// -------------------------------------------------------------- SECURITY
	User getCurrentUser(String domainName, int domain) throws AonSQLException;
	
	// -------------------------------------------------------------- ENTERPRISE
	LinkedList<Enterprise> getParentEnterprises(String domainName, int domain,
			String query) throws AonSQLException;
	Enterprise getEnterprise(String domainName, int domain, int id) throws AonSQLException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain) throws AonSQLException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain,int enterprise) throws AonSQLException;
	
	// -------------------------------------------------------------- ACCOUNT
	Account getAccount(String domainName,int domain,String code) throws AonSQLException;
	LinkedList<Account> getAccounts(String domainName,int domain,String query) throws AonSQLException;

	// -------------------------------------------------------------- CREDITOR
	LinkedList<Creditor> getBasicCreditors(String domainName,int domain,String query) throws AonSQLException;
}
