package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Fiscal")
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
}
