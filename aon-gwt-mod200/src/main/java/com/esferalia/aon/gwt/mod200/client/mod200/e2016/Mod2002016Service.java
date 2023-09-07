package com.esferalia.aon.gwt.mod200.client.mod200.e2016;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod2002016")
public interface Mod2002016Service extends RemoteService {
	// ---------------------------------------------------------------MODELO 200 - 2016
	Mod2002016 createMod2002016(String domainName,int domain,int year) throws AonCoreException;
	Mod2002016 initializeNewMod2002016(String domainName,int domain,Mod2002016 mod200) throws AonCoreException;
	Mod2002016 initializeMod2002016(String domainName,int domain,Mod2002016 mod200) throws AonCoreException;
	Mod2002016 getMod2002016ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002016 getMod2002016ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002016 calculateMod2002016(Mod2002016 mod200) throws AonCoreException;
	Mod2002016 saveMod2002016(String domainName,int domain,Mod2002016 mod200) throws AonCoreException;
	Mod2002016 validateMod2002016(Mod2002016 mod200) throws AonCoreException;
	void deleteMod2002016(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002016(Mod2002016 mod200) throws AonCoreException;
	Mod2002016 importMod2002015(String domainName,int domain,Mod2002016 mod200) throws AonCoreException;
	Mod2002016 fillMod2002016AccountingData(Mod2002016 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException;

}
