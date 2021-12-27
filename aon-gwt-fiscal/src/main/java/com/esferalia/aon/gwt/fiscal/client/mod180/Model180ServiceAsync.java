package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model180ServiceAsync {
	
	void getMod180s(Occam occam, AsyncCallback<LinkedList<Mod180>> callback);
	void get(Occam occam, Integer id, AsyncCallback<Mod180> callback);
	void save(Occam occam, Mod180 mod180, AsyncCallback<Mod180> callback);
	void delete(Occam occam, Mod180 mod180,AsyncCallback<Void> callback);
	void getDetail(Occam occam, Integer id, AsyncCallback<Mod180Detail> callback);
	void initialize(Occam occam, Integer year,AsyncCallback<Mod180> callback);
	void saveComments(Occam occam, Mod180 mod180,AsyncCallback<Mod180> asyncCallback);
	void changeStatus(Occam occam, Mod180 mod180, FiscalStatus newStatus, AsyncCallback<Mod180> callback);
	void duplicate(Occam occam, Mod180 mod180, AsyncCallback<Mod180> callback);

}
