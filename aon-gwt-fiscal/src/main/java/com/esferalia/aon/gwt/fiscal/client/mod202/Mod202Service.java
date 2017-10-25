package com.esferalia.aon.gwt.fiscal.client.mod202;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod202")
public interface Mod202Service extends RemoteService {

	Mod202 getMod202(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod202> getMod202s(String domainName, int domain) throws AonCoreException;
	Mod202 calculate(String domainName, Mod202 mod202) throws AonCoreException;
	Mod202 save(String domainName, Mod202 mod202) throws AonCoreException;
	void delete(String domainName, Mod202 mod202) throws AonCoreException;
	Mod202 initialize(String domainName, int domain, Mod202 mod202) throws AonCoreException;
	Mod202 saveComments(String domainName, Mod202 mod202) throws AonCoreException;
	Mod202 initializeForFinish(String domainName, Mod202 mod202) throws AonCoreException;
	Mod202 markAsFinished(String domainName, Mod202 mod202) throws AonCoreException;
	Mod202 markAsSent(String domainName, Mod202 mod202) throws AonCoreException;
	Mod202 markAsPending(String domainName, Mod202 mod202) throws AonCoreException;
	Mod202 create(String domainName, int domain, Mod202 mod202) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;

}
