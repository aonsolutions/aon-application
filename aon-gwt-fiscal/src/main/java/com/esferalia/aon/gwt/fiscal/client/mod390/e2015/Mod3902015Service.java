package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod3902015")
public interface Mod3902015Service extends RemoteService {

	Mod3902015 getMod3902015(String domainName, Integer domain,Integer id) throws AonCoreException;
	Mod3902015 saveMod3902015(String domainName, Integer domain,Mod3902015 mod390) throws AonCoreException;
	void deleteMod3902015(String domainName, Integer domain,Mod3902015 mod390) throws AonCoreException;
	Mod3902015 initializeMod3902015(String domainName, Integer domain, Integer year);

}
