package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model190ServiceAsync {

	void getMod190s(Occam occam, AsyncCallback<LinkedList<Mod190>> callback);
	void getMod190(Occam occam,Integer id, AsyncCallback<Mod190> callback);
	void delete(Occam occam,Mod190 mod190, AsyncCallback<Void> callback);
	void save(Occam occam,Mod190 mod190, AsyncCallback<Mod190> callback);
	void getDetail(Occam occam,Integer id, AsyncCallback<Mod190Detail> callback);
	void initialize(Occam occam, Integer year,AsyncCallback<Mod190> callback);
	void changeStatus(Occam occam, Mod190 mod190, FiscalStatus newStatus, AsyncCallback<Mod190> callback);
	void saveComments(Occam occam, Mod190 mod190, AsyncCallback<Mod190> callback);
	void duplicate(Occam occam, Mod190 mod190, AsyncCallback<Mod190> callback);

}
