package com.esferalia.aon.gwt.fiscal.client.mod202;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod202ServiceAsync {
	
	void getMod202(Occam occam, int id,AsyncCallback<Mod202> callback);
	void getMod202s(Occam occam,AsyncCallback<LinkedList<Mod202>> callback);
	void calculate(Occam occam, Mod202 mod202,AsyncCallback<Mod202> callback);
	void delete(Occam occam, Mod202 treeObject,AsyncCallback<Void> callback);
	void save(Occam occam, Mod202 mod202,AsyncCallback<Mod202> asyncCallback);
	void initialize(Occam occam, Mod202 mod202,AsyncCallback<Mod202> asyncCallback);
	void saveComments(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback);
	void initializeForFinish(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback);
	void markAsFinished(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback);
	void markAsSent(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback);
	void markAsPending(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback);
	void markAsCustomerCheck(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback);
	void create(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback);
	void getInfo(Occam occam, Mod202 mod202, IModelScript<Mod202Key> script,FiscalModelKeyInfo infoKey, AsyncCallback<String> callback);

}
