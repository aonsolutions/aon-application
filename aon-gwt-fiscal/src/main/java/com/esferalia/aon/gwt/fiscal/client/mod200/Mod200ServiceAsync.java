package com.esferalia.aon.gwt.fiscal.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod200ServiceAsync {
	void getMod200(String domainName, int domain, String user, Integer id, AsyncCallback<Mod200> asyncCallback);
	void getMod200s(String domainName, int domain, String user, AsyncCallback<LinkedList<Mod200>> asyncCallback);
}
