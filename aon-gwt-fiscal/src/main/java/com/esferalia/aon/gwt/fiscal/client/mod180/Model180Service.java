package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod180")
public interface Model180Service extends RemoteService {

	void deleteMod180(String domainName, String user, int domain,Mod180 mod180) throws AonCoreException;
	Mod180 saveMod180(String domainName, String user, int domain,Mod180 mod180) throws AonCoreException;
	LinkedList<Mod180> getMod180s(String domainName, String user, int domain) throws AonCoreException;
	Mod180 getMod180(String domainName, String user, int domain,Integer id) throws AonCoreException;
	Mod180Detail getMod180Detail(String domainName, String user, int domain,Integer id) throws AonCoreException;
	Mod180 initializeMod180(String domainName, String user, Integer domain, Integer year);
	Mod180 saveCommentsMod180(String domainName, String user, Mod180 mod180) throws AonCoreException;
	Mod180 changeStatusMod180(String domainName, String user, Mod180 mod180, FiscalStatus newStatus) throws AonCoreException;
	Mod180 duplicateNextYear(String domainName, String user, Integer domain,Integer id) throws AonCoreException;
	
}
