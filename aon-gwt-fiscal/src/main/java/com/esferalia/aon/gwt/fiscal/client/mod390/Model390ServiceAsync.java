package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model390ServiceAsync {

	void getMod390s(String domainName, Integer domain, String user, AsyncCallback<LinkedList<Mod390>> callback);
	void create(String domainName, int domain, String user, Mod390 mod390, AsyncCallback<Mod390> callback);
	void initialize(String domainName, int domain, String user, int year, AsyncCallback<Mod390> callback);
	void saveComments(String currentDomainName, String user, Mod390 mod390, AsyncCallback<Mod390> callback);
	
}
