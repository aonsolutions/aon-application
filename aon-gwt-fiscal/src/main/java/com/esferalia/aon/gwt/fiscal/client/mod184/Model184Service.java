package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod184")
public interface Model184Service extends RemoteService {

	void deleteMod184(String domainName, String user, int domain,Mod184 mod184) throws AonCoreException;
	Mod184 saveMod184(String domainName, String user, int domain,Mod184 mod184) throws AonCoreException;
	LinkedList<Mod184> getMod184s(String domainName, String user, int domain) throws AonCoreException;
	Mod184 getMod184(String domainName, String user, int domain,Integer id) throws AonCoreException;
	Mod184 initializeMod184(String domainName, String user, Integer domain, Integer year);
	Mod184 saveCommentsMod184(String domainName, String user, Mod184 mod184) throws AonCoreException;
	Mod184 changeStatusMod184(String domainName, String user, Mod184 mod184, FiscalStatus newStatus) throws AonCoreException;
	Mod184 duplicateNextYear(String domainName, String user, Integer domain,Integer id) throws AonCoreException;
}
