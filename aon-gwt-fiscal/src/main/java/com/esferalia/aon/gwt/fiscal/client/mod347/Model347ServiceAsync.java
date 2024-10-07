package com.esferalia.aon.gwt.fiscal.client.mod347;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model347ServiceAsync {
	
	void getMod347s(Occam occam, AsyncCallback<LinkedList<Mod347>> callback);
	void get(Occam occam, Integer id, AsyncCallback<Mod347> callback);
	void initialize(Occam occam, int year, AsyncCallback<Mod347> callback);
	void reset(Occam occam, Mod347 model, AsyncCallback<Mod347> asyncCallback);
	void save(Occam occam, Mod347 mod347, AsyncCallback<Mod347> callback);
	void delete(Occam occam, Mod347 mod347,AsyncCallback<Void> callback);
	void saveComments(Occam occam,Mod347 mod347,AsyncCallback<Mod347> asyncCallback);
	void changeStatus(Occam occam,Mod347 mod347, FiscalStatus newStatus, AsyncCallback<Mod347> callback);
	void getInfo(Occam occam, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback);
	void duplicate(Occam occam, Mod347 mod347, AsyncCallback<Mod347> callback);

}
