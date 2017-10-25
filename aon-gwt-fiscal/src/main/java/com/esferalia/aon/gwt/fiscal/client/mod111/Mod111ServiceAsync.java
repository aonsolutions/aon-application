package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod111ServiceAsync {
	
	void getMod111(String domainName, int domain, int id,AsyncCallback<Mod111> callback);
	void getMod111s(String domainName, int domain,AsyncCallback<LinkedList<Mod111>> callback);
	void calculate(String domainName, Mod111 mod111,AsyncCallback<Mod111> callback);
	void delete(String currentDomainName, Mod111 mod111,AsyncCallback<Void> callback);
	void save(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void saveComments(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void initializeForFinish(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void finish(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void reopen(String domainName, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void initialize(String domainName, int domain, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void create(String domainName, int domain, Mod111 mod111, AsyncCallback<Mod111> callback);
	void getInfo(String domainName, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void markAsSent(String currentDomainName, Mod111 currentMod111, AsyncCallback<Mod111> asyncCallback);
	
}
