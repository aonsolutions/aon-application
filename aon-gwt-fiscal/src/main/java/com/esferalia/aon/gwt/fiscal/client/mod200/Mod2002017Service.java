package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod2002017")
public interface Mod2002017Service extends RemoteService {
	// ---------------------------------------------------------------MODELO 200 - 2017
	Mod2002017 createMod2002017(String domainName,int domain,int year) throws AonCoreException;
	Mod2002017 initializeNewMod2002017(String domainName,int domain,Mod2002017 mod200) throws AonCoreException;
	Mod2002017 initializeMod2002017(String domainName,int domain,Mod2002017 mod200) throws AonCoreException;
	Mod2002017 getMod2002017ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002017 getMod2002017ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002017 calculateMod2002017(Mod2002017 mod200) throws AonCoreException;
	Mod2002017 saveMod2002017(String domainName,int domain,Mod2002017 mod200) throws AonCoreException;
	Mod2002017 validateMod2002017(Mod2002017 mod200) throws AonCoreException;
	void deleteMod2002017(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002017(Mod2002017 mod200) throws AonCoreException;
	Mod2002017 importMod2002016(String domainName,int domain,Mod2002017 mod200) throws AonCoreException;
	Mod2002017 fillMod2002017AccountingData(Mod2002017 mod200) throws AonCoreException;

}
