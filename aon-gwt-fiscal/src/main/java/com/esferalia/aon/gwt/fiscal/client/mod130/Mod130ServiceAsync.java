package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod130ServiceAsync {
	
	void getMod130(String domainName, int domain, int id,AsyncCallback<Mod130> callback);
	void getMod130s(String domainName, int domain,AsyncCallback<LinkedList<Mod130>> callback);
	void calculate(String domainName, Mod130 mod130,AsyncCallback<Mod130> callback);
	void delete(String currentDomainName, Mod130 mod130,AsyncCallback<Void> callback);
	void save(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void saveComments(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void initializeForFinish(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsFinished(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsSent(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsPending(String domainName, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void initialize(String domainName, int domain, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void create(String domainName, int domain, Mod130 mod130, AsyncCallback<Mod130> callback);
	void getInfo(String domainName, int domain, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	
}
