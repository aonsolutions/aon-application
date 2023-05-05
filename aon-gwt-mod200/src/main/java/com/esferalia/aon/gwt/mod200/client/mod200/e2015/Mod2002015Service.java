package com.esferalia.aon.gwt.mod200.client.mod200.e2015;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2015.Mod2002015;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Mod2002015")
public interface Mod2002015Service extends RemoteService {
	Mod2002015 createMod2002015(String domainName,int domain,int year) throws AonCoreException;
	Mod2002015 initializeNewMod2002015(String domainName,int domain,Mod2002015 mod200) throws AonCoreException;
	Mod2002015 initializeMod2002015(String domainName,int domain,Mod2002015 mod200) throws AonCoreException;
	Mod2002015 getMod2002015ByYear(String domainName,int domain, int year) throws AonCoreException;
	Mod2002015 getMod2002015ById(String domainName,int domain, int id) throws AonCoreException;
	Mod2002015 calculateMod2002015(Mod2002015 mod200) throws AonCoreException;
	Mod2002015 saveMod2002015(String domainName,int domain,Mod2002015 mod200) throws AonCoreException;
	Mod2002015 validateMod2002015(Mod2002015 mod200) throws AonCoreException;
	void deleteMod2002015(String domainName,int domain,int id) throws AonCoreException;
	String dumpAEATMod2002015(Mod2002015 mod200) throws AonCoreException;
	Mod2002015 importMod2002014(String domainName,int domain,Mod2002015 mod200) throws AonCoreException;
	Mod2002015 fillMod2002015AccountingData(Mod2002015 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException;

}
