package com.esferalia.aon.gwt.fiscal.client.mod390.e2014;

import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod3902014")
public interface Mod3902014Service extends RemoteService {

	Mod3902014 getMod3902014(String domainName, Integer domain,Integer id) throws AonCoreException;
	Mod3902014 saveMod3902014(String domainName, Integer domain,Mod3902014 mod390) throws AonCoreException;
	void deleteMod3902014(String domainName, Integer domain,Mod3902014 mod390) throws AonCoreException;
	Mod3902014 initializeMod3902014(String domainName, Integer domain, Integer year);
	
}
