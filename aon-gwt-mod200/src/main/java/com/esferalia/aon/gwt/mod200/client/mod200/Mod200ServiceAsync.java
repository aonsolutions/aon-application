package com.esferalia.aon.gwt.mod200.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod200ServiceAsync {
	void getMod200(Occam occam, Integer id, AsyncCallback<Mod200> asyncCallback);
	void getMod200s(Occam occam, AsyncCallback<LinkedList<Mod200>> asyncCallback);
	void saveComments(Occam occam, Mod200 mod200, AsyncCallback<Mod200> asyncCallback);
}
