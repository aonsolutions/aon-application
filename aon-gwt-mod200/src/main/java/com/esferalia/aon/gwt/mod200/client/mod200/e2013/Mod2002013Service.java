package com.esferalia.aon.gwt.mod200.client.mod200.e2013;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2013.Mod2002013;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod2002013")
public interface Mod2002013Service extends RemoteService {

	Mod2002013 createMod2002013(String domainName, int domain, int year) throws AonCoreException;
	Mod2002013 initializeNewMod2002013(String domainName,int domain,Mod2002013 mod200) throws AonCoreException;
	Mod2002013 initializeMod2002013(String domainName,int domain,Mod2002013 mod200) throws AonCoreException;
	Mod2002013 getMod2002013ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002013 getMod2002013ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002013 calculateMod2002013(Mod2002013 mod200) throws AonCoreException;
	Mod2002013 saveMod2002013(String domainName,int domain,Mod2002013 mod200) throws AonCoreException;
	Mod2002013 validateMod2002013(Mod2002013 mod200) throws AonCoreException;
	void deleteMod2002013(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002013(Mod2002013 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain) throws AonCoreException;
}
