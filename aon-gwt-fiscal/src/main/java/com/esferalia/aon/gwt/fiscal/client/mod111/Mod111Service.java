package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod111")
public interface Mod111Service extends RemoteService {

	Mod111 getMod111(String domainName, String userLogin, int domain, int id) throws AonCoreException;
	LinkedList<Mod111> getMod111s(String domainName, String userLogin, int domain) throws AonCoreException;
	Mod111 calculate(String domainName, String userLogin, Mod111 mod111) throws AonCoreException;
	Mod111 save(String domainName, String userLogin, Mod111 mod111) throws AonCoreException;
	Mod111 saveComments(String domainName, String userLogin, Mod111 mod111) throws AonCoreException;
	Mod111 initializeForFinish(String domainName, String userLogin, Mod111 mod111) throws AonCoreException;
	Mod111 markAsFinished(String domainName, String userLogin, Mod111 mod123) throws AonCoreException;
	Mod111 markAsPending(String domainName, String userLogin, Mod111 mod123) throws AonCoreException;
	Mod111 markAsSent(String domainName, String userLogin, Mod111 mod123) throws AonCoreException;
	void delete(String domainName, String userLogin, Mod111 mod111) throws AonCoreException;
	Mod111 initialize(String domainName, String userLogin, int domain, Mod111 mod111);
	Mod111 create(String domainName, String userLogin, int domain, Mod111 mod111) throws AonCoreException;
	String getInfo(String domainName, String userLogin, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Double mathExpression(String expression) throws AonCoreException;

	Integer validationFile(String domainName, Integer domainId, String user, Integer id);
	Integer presentationFile(String domainName, Integer domainId, String user, Integer id);
}
