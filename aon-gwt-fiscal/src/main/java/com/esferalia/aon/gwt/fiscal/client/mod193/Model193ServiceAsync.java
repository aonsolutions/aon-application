package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model193ServiceAsync {

	void getMod193s(Occam occam, AsyncCallback<LinkedList<Mod193>> callback);
	void get(Occam occam,Integer id, AsyncCallback<Mod193> callback);
	void delete(Occam occam,Mod193 mod193, AsyncCallback<Void> callback);
	void save(Occam occam,Mod193 mod193, AsyncCallback<Mod193> callback);
	void initialize(Occam occam, Integer year,AsyncCallback<Mod193> callback);
	void changeStatus(Occam occam, Mod193 mod193, FiscalStatus newStatus, AsyncCallback<Mod193> callback);
	void saveComments(Occam occam, Mod193 mod193, AsyncCallback<Mod193> callback);
	void duplicate(Occam occam, Mod193 mod193, AsyncCallback<Mod193> callback);

}
