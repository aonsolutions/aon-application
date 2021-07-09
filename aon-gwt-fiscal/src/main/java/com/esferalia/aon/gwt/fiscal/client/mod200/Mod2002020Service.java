package com.esferalia.aon.gwt.fiscal.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod2002020")
public interface Mod2002020Service extends RemoteService {
	
	// MODELO 200 - 2020	
	Mod2002020 createMod2002020(String domainName, int domain, String user, int year) throws AonCoreException;
	Mod2002020 initializeNewMod2002020(String domainName, int domain, String user, Mod2002020 mod200);
	Mod2002020 initializeMod2002020(String domainName, int domain, String user, Mod2002020 mod200);
	Mod2002020 getMod2002020ByYear(String domainName, int domain, String user, int year) throws AonCoreException;
	Mod2002020 getMod2002020ById(String domainName, int domain, String user, int id) throws AonCoreException;
	Mod2002020 calculateMod2002020(Mod2002020 mod200) throws AonCoreException;
	Mod2002020 saveMod2002020(String domainName, int domain, String user, Mod2002020 mod200) throws AonCoreException;
	Mod2002020 validateMod2002020(Mod2002020 mod200) throws AonCoreException;
	void deleteMod2002020(String domainName, int domain, String user, int id) throws AonCoreException;
	String dumpAEATMod2002020(Mod2002020 mod200) throws AonCoreException;
	Mod2002020 importMod2002019(String domainName, int domain, String user, Mod2002020 mod200) throws AonCoreException;
	Mod2002020 fillMod2002020AccountingData(String domainName, int domain, String user, Mod2002020 mod200, String data);
	LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain, String user) throws AonCoreException;

}
