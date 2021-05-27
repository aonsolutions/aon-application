package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod3902015")
public interface Mod3902015Service extends RemoteService {

	Mod3902015 getMod3902015(String domainName, Integer domain,String user, Mod390 mod390) throws AonCoreException;
	Mod3902015 saveMod3902015(String domainName, Integer domain,String user,Mod3902015 mod390) throws AonCoreException;
	void deleteMod3902015(String domainName, Integer domain,String user,Mod3902015 mod390) throws AonCoreException;
	Mod3902015 changeStatus(String domainName,String user, Mod3902015 mod390, FiscalStatus status);
	Integer presentationFile(String domainName, Integer domainId,String user, Integer id);

}
