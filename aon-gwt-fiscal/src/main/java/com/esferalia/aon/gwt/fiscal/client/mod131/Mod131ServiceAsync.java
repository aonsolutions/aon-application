package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod131ServiceAsync {

	void getMod131(String domainName, int domain, int id,AsyncCallback<Mod131> callback);
	void getMod131s(String domainName, int domain,AsyncCallback<LinkedList<Mod131>> callback);
	void calculate(String domainName, Mod131 mod131,AsyncCallback<Mod131> callback);
	void calculateActivity(String domainName, int domain, Mod131Activity activity, AsyncCallback<Mod131Activity> callback);
	void delete(String currentDomainName, Mod131 mod131,AsyncCallback<Void> callback);
	void save(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void saveComments(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void initializeForFinish(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void markAsFinished(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void markAsSent(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void markAsPending(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void initialize(String domainName, int domain, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void create(String domainName, int domain, Mod131 mod131, AsyncCallback<Mod131> callback);
	void getInfo(String domainName, int domain, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	

}
