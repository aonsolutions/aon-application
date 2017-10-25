package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod123ServiceAsync {
	
	void getMod123(String domainName, int domain, int id,AsyncCallback<Mod123> callback);
	void getMod123s(String domainName, int domain,AsyncCallback<LinkedList<Mod123>> callback);
	void calculate(String domainName, Mod123 mod123,AsyncCallback<Mod123> callback);
	void delete(String currentDomainName, Mod123 mod123,AsyncCallback<Void> callback);
	void save(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void saveComments(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void initializeForFinish(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void markAsFinished(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void markAsSent(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void markAsPending(String domainName, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void initialize(String domainName, int domain, Mod123 mod123,AsyncCallback<Mod123> asyncCallback);
	void create(String domainName, int domain, Mod123 mod123, AsyncCallback<Mod123> callback);
	void getInfo(String domainName, int domain, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
}
