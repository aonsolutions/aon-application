package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model184ServiceAsync {

	void getMod184s(Occam occam, AsyncCallback<LinkedList<Mod184>> callback);
	void get(Occam occam,Integer id, AsyncCallback<Mod184> callback);
	void delete(Occam occam,Mod184 mod184, AsyncCallback<Void> callback);
	void save(Occam occam,Mod184 mod184, AsyncCallback<Mod184> callback);
	void initialize(Occam occam, Integer year,AsyncCallback<Mod184> callback);
	void saveComments(Occam occam, Mod184 mod184,AsyncCallback<Mod184> asyncCallback);
	void changeStatus(Occam occam, Mod184 mod184, FiscalStatus newStatus, AsyncCallback<Mod184> callback);
	void duplicate(Occam occam, Mod184 mod184, AsyncCallback<Mod184> callback);

}
