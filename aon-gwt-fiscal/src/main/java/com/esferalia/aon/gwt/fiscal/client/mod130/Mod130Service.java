package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod130")
public interface Mod130Service extends RemoteService {
	
	Mod130 getMod130(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod130> getMod130s(String domainName, int domain) throws AonCoreException;
	Mod130 calculate(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 save(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 saveComments(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 initializeForFinish(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 markAsFinished(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 markAsSent(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 markAsPending(String domainName, Mod130 mod130) throws AonCoreException;
	void delete(String domainName, Mod130 mod130) throws AonCoreException;
	Mod130 initialize(String domainName, int domain, Mod130 mod130);
	Mod130 create(String domainName, int domain, Mod130 mod130) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

}
