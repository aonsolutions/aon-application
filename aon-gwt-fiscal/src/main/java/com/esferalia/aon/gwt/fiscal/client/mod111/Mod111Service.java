package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod111")
public interface Mod111Service extends RemoteService {

	Mod111 getMod111(String domainName, int domain, int id) throws AonCoreException;
	LinkedList<Mod111> getMod111s(String domainName, int domain) throws AonCoreException;
	Mod111 calculate(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 save(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 saveComments(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 initializeForFinish(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 finish(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 reopen(String domainName, Mod111 mod111) throws AonCoreException;
	void delete(String domainName, Mod111 mod111) throws AonCoreException;
	Mod111 initialize(String domainName, int domain, Mod111 mod111);
	Mod111 create(String domainName, int domain, Mod111 mod111) throws AonCoreException;
	String getInfo(String domainName, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Mod111 markAsSent(String currentDomainName, Mod111 currentMod111) throws AonCoreException;

}
