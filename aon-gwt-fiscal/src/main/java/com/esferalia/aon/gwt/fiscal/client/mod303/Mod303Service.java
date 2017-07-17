package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod303")
public interface Mod303Service extends RemoteService {

	// ---------------------------------------------------------------MODELO 303
	Mod303 getMod303(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod303> getMod303s(String domainName, int domain) throws AonCoreException;
	Mod303 calculateMod303(String domainName, Mod303 mod303) throws AonCoreException;
	Mod303 saveMod303(String domainName, Mod303 mod303) throws AonCoreException;
	Mod303 saveCommentsMod303(String domainName, Mod303 mod303) throws AonCoreException;
	Mod303 initializeForFinishMod303(String domainName, Mod303 mod303) throws AonCoreException;
	Mod303 finishMod303(String domainName, Mod303 mod303) throws AonCoreException;
	Mod303 reopenMod303(String domainName, Mod303 mod303) throws AonCoreException;
	void deleteMod303(String domainName, Mod303 mod303) throws AonCoreException;
	Mod303 initializeMod303(String domainName, int domain, Mod303 mod303);
	Mod303 createMod303(String domainName, int domain, Mod303 mod303) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

}
