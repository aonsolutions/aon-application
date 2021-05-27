package com.esferalia.aon.gwt.fiscal.client.mod115;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod115ServiceAsync {
	
	void getMod115(String domainName, String user, int domain, int id,AsyncCallback<Mod115> callback);
	void getMod115s(String domainName, String user, int domain,AsyncCallback<LinkedList<Mod115>> callback);
	void calculate(String domainName, String user, Mod115 mod115,AsyncCallback<Mod115> callback);
	void delete(String domainName, String user, Mod115 mod115,AsyncCallback<Void> callback);
	void save(String domainName, String user, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void saveComments(String domainName, String user, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void initializeForFinish(String domainName, String user, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void markAsFinished(String domainName, String user, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void markAsPending(String domainName, String user, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void markAsSent(String domainName, String user, Mod115 mod115, AsyncCallback<Mod115> callback);
	void markAsCustomerCheck(String domainName, String user, Mod115 mod115, AsyncCallback<Mod115> callback);
	void initialize(String domainName, String user, int domain, Mod115 mod115,AsyncCallback<Mod115> asyncCallback);
	void create(String domainName, String user, int domain, Mod115 mod115, AsyncCallback<Mod115> callback);
	void getInfo(String domainName, String user, int domain, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void mathExpression(String expression, AsyncCallback<Double> callback );
	void validationFile(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Integer> callback);
	void presentationFile(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Integer> callback);
}
