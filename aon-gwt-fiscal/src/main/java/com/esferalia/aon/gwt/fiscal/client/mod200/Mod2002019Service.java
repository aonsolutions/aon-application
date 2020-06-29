package com.esferalia.aon.gwt.fiscal.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod2002019")
public interface Mod2002019Service extends RemoteService {
	// ---------------------------------------------------------------MODELO 200 - 2019
	Mod2002019 createMod2002019(String domainName,int domain,int year) throws AonCoreException;
	Mod2002019 initializeNewMod2002019(String domainName,int domain,Mod2002019 mod200) throws AonCoreException;
	Mod2002019 initializeMod2002019(String domainName,int domain,Mod2002019 mod200) throws AonCoreException;
	Mod2002019 getMod2002019ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002019 getMod2002019ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002019 calculateMod2002019(Mod2002019 mod200) throws AonCoreException;
	Mod2002019 saveMod2002019(String domainName,int domain,Mod2002019 mod200) throws AonCoreException;
	Mod2002019 validateMod2002019(Mod2002019 mod200) throws AonCoreException;
	void deleteMod2002019(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002019(Mod2002019 mod200) throws AonCoreException;
	Mod2002019 importMod2002018(String domainName,int domain,Mod2002019 mod200) throws AonCoreException;
	Mod2002019 fillMod2002019AccountingData(Mod2002019 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException;

}
