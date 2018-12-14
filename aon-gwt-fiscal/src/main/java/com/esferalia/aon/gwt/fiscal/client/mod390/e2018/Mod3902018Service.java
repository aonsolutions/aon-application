package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod3902018")
public interface Mod3902018Service extends RemoteService {

	Mod3902018 getMod3902018(String domainName, Integer domain,Mod390 mod390) throws AonCoreException;
	Mod3902018 saveMod3902018(String domainName, Integer domain,Mod3902018 mod390) throws AonCoreException;
	void deleteMod3902018(String domainName, Integer domain,Mod3902018 mod390) throws AonCoreException;
	Mod3902018 changeStatus(String domainName, Mod3902018 mod390, FiscalStatus status);
	Integer presentationFile(String domainName, Integer domainId, String user, Integer id);

}
