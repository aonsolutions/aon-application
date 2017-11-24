package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model184ServiceAsync {

	void deleteMod184(String domainName, int domain,Mod184 mod184, AsyncCallback<Void> callback);
	void saveMod184(String domainName, int domain,Mod184 mod184, AsyncCallback<Mod184> callback);
	void getMod184s(String domainName, int domain, AsyncCallback<LinkedList<Mod184>> callback);
	void getMod184(String domainName, int domain,Integer id, AsyncCallback<Mod184> callback);
	void initializeMod184(String domainName, Integer domain, Integer year,AsyncCallback<Mod184> callback);
	void saveCommentsMod184(String domainName, Mod184 mod184,AsyncCallback<Mod184> asyncCallback);
	void changeStatusMod184(String domainName, Mod184 mod184, FiscalStatus newStatus, AsyncCallback<Mod184> callback);

}
