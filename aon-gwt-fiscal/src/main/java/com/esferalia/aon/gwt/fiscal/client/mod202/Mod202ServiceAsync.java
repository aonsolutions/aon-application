package com.esferalia.aon.gwt.fiscal.client.mod202;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod202ServiceAsync {
	
	void getMod202(String domainName, int domain, int id,AsyncCallback<Mod202> callback);
	void getMod202s(String domainName, int domain,AsyncCallback<LinkedList<Mod202>> callback);
	void calculate(String domainName, Mod202 mod202,AsyncCallback<Mod202> callback);
	void delete(String currentDomainName, Mod202 treeObject,AsyncCallback<Void> callback);
	void save(String domainName, Mod202 mod202,AsyncCallback<Mod202> asyncCallback);
	void initialize(String domainName, int domain, Mod202 mod202,AsyncCallback<Mod202> asyncCallback);
	void saveComments(String domainName, Mod202 mod202, AsyncCallback<Mod202> callback);
	void initializeForFinish(String domainName, Mod202 mod202, AsyncCallback<Mod202> callback);
	void markAsFinished(String domainName, Mod202 mod202, AsyncCallback<Mod202> callback);
	void markAsSent(String domainName, Mod202 mod202, AsyncCallback<Mod202> callback);
	void markAsPending(String domainName, Mod202 mod202, AsyncCallback<Mod202> callback);
	void create(String domainName, int domain, Mod202 mod202, AsyncCallback<Mod202> callback);
	void getInfo(String domainName, int domain, Mod202 mod202, IModelScript<Mod202Key> script,FiscalModelKeyInfo infoKey, AsyncCallback<String> callback);
	

}
