package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod303ServiceAsync {
	// ---------------------------------------------------------------MODELO 303
	void getMod303(String domainName,String user, int domain, int id,AsyncCallback<Mod303> callback);
	void getMod303s(String domainName,String user, int domain,AsyncCallback<LinkedList<Mod303>> callback);
	void initialize(String domainName,String user, int domain, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void create(String domainName,String user, int domain, Mod303 mod303, AsyncCallback<Mod303> callback);
	void declarationChanged(String domainName,String user, int domain, Mod303 mod303, AsyncCallback<Mod303> callback);

	void delete(String domainName,String user, Mod303 mod303,AsyncCallback<Void> callback);
	void save(String domainName,String user, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void saveComments(String domainName,String user, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void initializeForFinish(String domainName,String user, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void calculate(String domainName,String user, Mod303 mod303,AsyncCallback<Mod303> callback);
	void getInfo(String domainName,String user, int domain, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void importMod303(String domainName,String user, int currentDomain, AsyncCallback<Void> asyncCallback);
	void markAsFinished(String domainName,String user, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void markAsPending(String domainName,String user, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void markAsSent(String domainName,String user, Mod303 mod303, AsyncCallback<Mod303> asyncCallback);
	void mathExpression(String expression, AsyncCallback<Double> callback );

}
