package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod303ServiceAsync {
	// ---------------------------------------------------------------MODELO 303
	void getMod303(String domainName, int domain, int id,AsyncCallback<Mod303> callback);
	void getMod303s(String domainName, int domain,AsyncCallback<LinkedList<Mod303>> callback);
	void calculate(String domainName, Mod303 mod303,AsyncCallback<Mod303> callback);
	void delete(String currentDomainName, Mod303 mod303,AsyncCallback<Void> callback);
	void save(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void saveComments(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void initializeForFinish(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void markAsFinished(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void markAsPending(String domainName, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void initialize(String domainName, int domain, Mod303 mod303,AsyncCallback<Mod303> asyncCallback);
	void create(String domainName, int domain, Mod303 mod303, AsyncCallback<Mod303> callback);
	void getInfo(String domainName, int domain, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void declarationChanged(String domainName, int domain, Mod303 mod303, AsyncCallback<Mod303> callback);
	void importMod303(String currentDomainName, int currentDomain, AsyncCallback<Void> asyncCallback);
	void markAsSent(String currentDomainName, Mod303 mod303, AsyncCallback<Mod303> asyncCallback);

}
