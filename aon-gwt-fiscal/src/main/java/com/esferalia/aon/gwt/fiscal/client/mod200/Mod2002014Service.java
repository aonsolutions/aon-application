package com.esferalia.aon.gwt.fiscal.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod2002014")
public interface Mod2002014Service extends RemoteService {

	Mod2002014 createMod2002014(String domainName,int domain,int year) throws AonCoreException;
	Mod2002014 initializeNewMod2002014(String domainName,int domain,Mod2002014 mod200) throws AonCoreException;
	Mod2002014 initializeMod2002014(String domainName,int domain,Mod2002014 mod200) throws AonCoreException;
	Mod2002014 getMod2002014ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002014 getMod2002014ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002014 calculateMod2002014(Mod2002014 mod200) throws AonCoreException;
	Mod2002014 saveMod2002014(String domainName,int domain,Mod2002014 mod200) throws AonCoreException;
	Mod2002014 validateMod2002014(Mod2002014 mod200) throws AonCoreException;
	void deleteMod2002014(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002014(Mod2002014 mod200) throws AonCoreException;
	Mod2002014 importMod2002013(String domainName,int domain,Mod2002014 mod200) throws AonCoreException;
	Mod2002014 fillMod2002014AccountingData(Mod2002014 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException;

}
