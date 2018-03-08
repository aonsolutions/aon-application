package com.esferalia.aon.gwt.fiscal.client.mod115;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod115")
public interface Mod115Service extends RemoteService {

	Mod115 getMod115(String domainName, String user,int domain, int id) throws AonCoreException;
	LinkedList<Mod115> getMod115s(String domainName, String user, int domain) throws AonCoreException;
	Mod115 calculate(String domainName, String user, Mod115 mod115) throws AonCoreException;
	Mod115 save(String domainName, String user, Mod115 mod115) throws AonCoreException;
	Mod115 saveComments(String domainName, String user, Mod115 mod115) throws AonCoreException;
	Mod115 initializeForFinish(String domainName, String user, Mod115 mod115) throws AonCoreException;
	Mod115 markAsFinished(String domainName, String user, Mod115 mod115) throws AonCoreException;
	Mod115 markAsPending(String domainName, String user, Mod115 mod115) throws AonCoreException;
	Mod115 markAsSent(String domainName, String user, Mod115 mod115) throws AonCoreException;
	void delete(String domainName, String user, Mod115 mod115) throws AonCoreException;
	Mod115 initialize(String domainName, String user, int domain, Mod115 mod115);
	Mod115 create(String domainName, String user, int domain, Mod115 mod115) throws AonCoreException;
	String getInfo(String domainName, String user, int domain, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Double mathExpression(String expression) throws AonCoreException;

	Integer validationFile(String domainName, Integer domainId, String user, Integer id);
	Integer presentationFile(String domainName, Integer domainId, String user, Integer id);
}
