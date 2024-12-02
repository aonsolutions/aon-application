package com.esferalia.aon.gwt.mod200.client.mod200.e2018;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod2002018")
public interface Mod2002018Service extends RemoteService {
	// ---------------------------------------------------------------MODELO 200 - 2018
	Mod2002018 createMod2002018(String domainName,int domain,int year) throws AonCoreException;
	Mod2002018 initializeNewMod2002018(String domainName,int domain,Mod2002018 mod200) throws AonCoreException;
	Mod2002018 initializeMod2002018(String domainName,int domain,Mod2002018 mod200) throws AonCoreException;
	Mod2002018 getMod2002018ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002018 getMod2002018ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002018 calculateMod2002018(Mod2002018 mod200) throws AonCoreException;
	Mod2002018 saveMod2002018(String domainName,int domain,Mod2002018 mod200) throws AonCoreException;
	Mod2002018 validateMod2002018(Mod2002018 mod200) throws AonCoreException;
	void deleteMod2002018(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002018(Mod2002018 mod200) throws AonCoreException;
	Mod2002018 importMod2002017(String domainName,int domain,Mod2002018 mod200) throws AonCoreException;
	Mod2002018 fillMod2002018AccountingData(Mod2002018 mod200, String data) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException;

}
