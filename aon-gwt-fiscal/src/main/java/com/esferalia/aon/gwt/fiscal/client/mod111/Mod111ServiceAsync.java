package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod111ServiceAsync {
	
	void getMod111(String domainName, String userLogin, int domain, int id,AsyncCallback<Mod111> callback);
	void getMod111s(String domainName, String userLogin, int domain,AsyncCallback<LinkedList<Mod111>> callback);
	void calculate(String domainName, String userLogin, Mod111 mod111,AsyncCallback<Mod111> callback);
	void delete(String currentDomainName, String userLogin, Mod111 mod111,AsyncCallback<Void> callback);
	void save(String domainName, String userLogin, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void saveComments(String domainName, String userLogin, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void initializeForFinish(String domainName, String userLogin, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void markAsFinished(String domainName, String userLogin, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void markAsPending(String domainName, String userLogin, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void markAsSent(String domainName, String userLogin, Mod111 mod111, AsyncCallback<Mod111> asyncCallback);
	void markAsCustomerCheck(String currentDomainName, String currentUser, Mod111 currentMod, AsyncCallback<Mod111> asyncCallback);
	void initialize(String domainName, String userLogin, int domain, Mod111 mod111,AsyncCallback<Mod111> asyncCallback);
	void create(String domainName, String userLogin, int domain, Mod111 mod111, AsyncCallback<Mod111> callback);
	void getInfo(String domainName, String userLogin, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey,AsyncCallback<String> callback);
	void mathExpression(String expression, AsyncCallback<Double> callback );
	void validationFile(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Integer> callback);
	void presentationFile(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Integer> callback);
	
	
}
