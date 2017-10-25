package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod123")
public interface Mod123Service extends RemoteService {
	
	Mod123 getMod123(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod123> getMod123s(String domainName, int domain) throws AonCoreException;
	Mod123 calculate(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 save(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 saveComments(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 initializeForFinish(String domainName, Mod123 mod123) throws AonCoreException;
	void delete(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 markAsFinished(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 markAsPending(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 markAsSent(String domainName, Mod123 mod123) throws AonCoreException;
	Mod123 initialize(String domainName, int domain, Mod123 mod123);
	Mod123 create(String domainName, int domain, Mod123 mod123) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

}
