package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod130ServiceAsync {
	
	void getMod130(String domainName, String user, int domain, int id,AsyncCallback<Mod130> callback);
	void getMod130s(String domainName, String user, int domain,AsyncCallback<LinkedList<Mod130>> callback);
	void calculate(String domainName, String user, Mod130 mod130,AsyncCallback<Mod130> callback);
	void delete(String domainName, String user, Mod130 mod130,AsyncCallback<Void> callback);
	void save(String domainName, String user, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void saveComments(String domainName, String user, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void initializeForFinish(String domainName, String user, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsFinished(String domainName, String user, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsSent(String domainName, String user, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsPending(String domainName, String user, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void markAsCustomerCheck(String domainName, String user, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void initialize(String domainName, String user, int domain, Mod130 mod130,AsyncCallback<Mod130> asyncCallback);
	void create(String domainName, String user, int domain, Mod130 mod130, AsyncCallback<Mod130> callback);
	void getInfo(String domainName, String user, int domain, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void mathExpression(String expression, AsyncCallback<Double> callback);
	void presentationFile(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Integer> callback);
}
