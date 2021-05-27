package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model190ServiceAsync {

	void getMod190s(String domainName, String user, int domain, AsyncCallback<LinkedList<Mod190>> callback);
	void getMod190(String domainName, String user, int domain,Integer id, AsyncCallback<Mod190> callback);
	void delete(String domainName, String user, int domain,Mod190 mod190, AsyncCallback<Void> callback);
	void save(String domainName, String user, int domain,Mod190 mod190, AsyncCallback<Mod190> callback);
	void getDetail(String domainName, String user, int domain,Integer id, AsyncCallback<Mod190Detail> callback);
	void initialize(String domainName, String user, Integer domain, Integer year,AsyncCallback<Mod190> callback);
	void changeStatus(String domainName, String user, Mod190 mod190, FiscalStatus newStatus, AsyncCallback<Mod190> callback);
	void saveComments(String domainName, String user, Mod190 mod190, AsyncCallback<Mod190> callback);
	void duplicateNextYear(String domainName, String user, Integer domain, Integer id, AsyncCallback<Mod190> callback);

}
