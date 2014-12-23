package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod200")
public interface Mod200Service extends RemoteService {

	Mod200 getMod200(String domainName,int domain, int year) throws AonSQLException;
	Mod200 initialize(String domainName,int domain,Mod200 mod200) throws AonSQLException;
	Mod200 calculate(Mod200 mod200) throws AonSQLException;
	Mod200 save(String domainName,int domain,Mod200 mod200) throws AonSQLException;
	Mod200 validate(Mod200 mod200) throws AonSQLException;
	Mod200 delete(String domainName,int domain,Mod200 mod200) throws AonSQLException;
	String dumpAEAT(Mod200 mod200) throws AonSQLException;
	ArrayList<CompanyBank> getCompanyBanks(String domainName,int domain,int enterprise) throws AonSQLException;

	
}
