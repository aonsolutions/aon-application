package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod303")
public interface Mod303Service extends RemoteService {

	// ---------------------------------------------------------------MODELO 303
	Mod303 getMod303(String domainName,String user, int domain, int id) throws AonCoreException;
	LinkedList<Mod303> getMod303s(String domainName,String user, int domain) throws AonCoreException;
	Mod303 initialize(String domainName,String user, int domain, Mod303 mod303);
	Mod303 create(String domainName,String user, int domain, Mod303 mod303) throws AonCoreException;
	Mod303 declarationChanged(String domainName,String user, int domain, Mod303 mod303) throws AonCoreException;
	
	void delete(String domainName,String user, Mod303 mod303) throws AonCoreException;
	Mod303 save(String domainName,String user, Mod303 mod303) throws AonCoreException;
	Mod303 saveComments(String domainName,String user, Mod303 mod303) throws AonCoreException;
	Mod303 initializeForFinish(String domainName,String user, Mod303 mod303) throws AonCoreException;
	Mod303 calculate(String domainName,String user, Mod303 mod303) throws AonCoreException;
	String getInfo(String domainName,String user, int domain, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
//	void importMod303(String domainName,String user, int currentDomain) throws AonCoreException;
	Mod303 markAsFinished(String domainName,String user, Mod303 mod303) throws AonCoreException;
	Mod303 markAsPending(String domainName,String user, Mod303 mod303) throws AonCoreException;
	Mod303 markAsSent(String domainName,String user, Mod303 mod303) throws AonCoreException;

	Double mathExpression(String expression) throws AonCoreException;
	
	Integer validationFile(String domainName, Integer domainId, String user, Integer id);
	Integer presentationFile(String domainName, Integer domainId, String user, Integer id);
}
