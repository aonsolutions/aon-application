package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod349")
public interface Model349Service extends RemoteService {
	
	// ---------------------------------------------------------------MODELO 349 
	void deleteMod349(String domainName, int domain,Mod349 mod349) throws AonCoreException;
	Mod349 saveMod349(String domainName, int domain,Mod349 mod349) throws AonCoreException;
	LinkedList<Mod349> getMod349s(String domainName, int domain) throws AonCoreException;
	Mod349 getMod349(String domainName, int domain,Integer id) throws AonCoreException;
	Mod349Detail getMod349Detail(String domainName, int domain, Integer id) throws AonCoreException;
	Mod349 initializeMod349(String domainName, Integer domain);
	Mod349 saveCommentsMod349(String domainName, Mod349 mod349) throws AonCoreException;
	Mod349 changeStatusMod349(String domainName, Mod349 mod349, FiscalStatus newStatus) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) throws AonCoreException;
}
