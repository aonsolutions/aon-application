package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod131")
public interface Mod131Service extends RemoteService {

	Mod131 getMod131(String domainName, String user, int domain, int id) throws AonCoreException;
	LinkedList<Mod131> getMod131s(String domainName, String user, int domain) throws AonCoreException;
	Mod131 calculate(String domainName, String user, Mod131 mod131) throws AonCoreException;
	Mod131Activity calculateActivity(String domainName, String user, int domain, Mod131Activity activity) throws AonCoreException;
	Mod131 save(String domainName, String user, Mod131 mod131) throws AonCoreException;
	Mod131 saveComments(String domainName, String user, Mod131 mod131) throws AonCoreException;
	Mod131 initializeForFinish(String domainName, String user, Mod131 mod131) throws AonCoreException;
	Mod131 markAsFinished(String domainName, String user, Mod131 mod131) throws AonCoreException;
	Mod131 markAsSent(String domainName, String user, Mod131 mod131) throws AonCoreException;
	Mod131 markAsPending(String domainName, String user, Mod131 mod131) throws AonCoreException;
	void delete(String domainName, String user, Mod131 mod131) throws AonCoreException;
	Mod131 initialize(String domainName, String user, int domain, Mod131 mod131);
	Mod131 create(String domainName, String user, int domain, Mod131 mod131) throws AonCoreException;
	String getInfo(String domainName, String user, int domain, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) throws AonCoreException;
	Double mathExpression(String expression) throws AonCoreException;
	
	Integer presentationFile(String domainName, Integer domainId, String user, Integer id);

}
