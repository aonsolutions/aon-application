package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model390ServiceAsync {

	void getMod390(Occam occam, Integer id, AsyncCallback<Mod390> callback);
	void getMod390s(Occam occam, AsyncCallback<LinkedList<Mod390>> callback);
//	void create(Occam occam, Mod390 mod390, AsyncCallback<Mod390> callback);
	void initialize(Occam occam, int year, AsyncCallback<Mod390> callback);
	void saveComments(Occam occam, Mod390 mod390, AsyncCallback<Mod390> callback);
	void delete(Occam occam, Mod390 mod390, AsyncCallback<Void> callback);
	
}
