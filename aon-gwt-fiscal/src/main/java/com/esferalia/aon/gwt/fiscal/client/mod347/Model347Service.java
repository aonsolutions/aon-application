package com.esferalia.aon.gwt.fiscal.client.mod347;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod347")
public interface Model347Service extends RemoteService {
	
	// ---------------------------------------------------------------MODELO 347 
	void deleteMod347(String domainName, int domain,Mod347 mod347) throws AonCoreException;
	Mod347 saveMod347(String domainName, int domain,Mod347 mod347) throws AonCoreException;
	LinkedList<Mod347> getMod347s(String domainName, int domain) throws AonCoreException;
	Mod347 getMod347(String domainName, int domain,Integer id) throws AonCoreException;
	Mod347 initializeMod347(String domainName, Integer domain);
	Mod347 saveCommentsMod347(String domainName, Mod347 mod347) throws AonCoreException;
	Mod347 changeStatusMod347(String domainName, Mod347 mod347, FiscalStatus newStatus) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Mod347 duplicateNextYear(String domainName, Integer domain,Integer id) throws AonCoreException;
	
}
