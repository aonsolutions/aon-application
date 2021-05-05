package com.esferalia.aon.gwt.fiscal.client.mod390HF;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod390HF")
public interface Mod390HFService extends RemoteService {

	// ---------------------------------------------------------------MODELO 303
	Mod390HF getMod390HF(String domainName, int domain, String user, int id) throws AonCoreException;
	LinkedList<Mod390HF> getMod390HFs(String domainName, int domain, String user) throws AonCoreException;
	Mod390HF initialize(String domainName, int domain, String user, Mod390HF mod);
	Mod390HF create(String domainName, int domain, String user, Mod390HF mod) throws AonCoreException;
	Mod390HF declarationChanged(String domainName, int domain, String user, Mod390HF mod) throws AonCoreException;
	
	void delete(String domainName, String user, Mod390HF mod) throws AonCoreException;
	Mod390HF save(String domainName, String user, Mod390HF mod) throws AonCoreException;
	Mod390HF saveComments(String domainName, String user, Mod390HF mod) throws AonCoreException;
	Mod390HF initializeForFinish(String domainName, String user, Mod390HF mod) throws AonCoreException;
	Mod390HF calculate(String domainName, String user, Mod390HF mod) throws AonCoreException;
	String getInfo(String domainName, int domain, String user, Mod390HF mod, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Mod390HF markAsFinished(String domainName, String user, Mod390HF mod) throws AonCoreException;
	Mod390HF markAsPending(String domainName, String user, Mod390HF mod) throws AonCoreException;
	Mod390HF markAsSent(String currentDomainName, String user, Mod390HF mod) throws AonCoreException;

}
