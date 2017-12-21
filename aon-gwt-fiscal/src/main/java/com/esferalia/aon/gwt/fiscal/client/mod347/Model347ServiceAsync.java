package com.esferalia.aon.gwt.fiscal.client.mod347;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model347ServiceAsync {
	
	// ---------------------------------------------------------------MODELO 347
	void deleteMod347(String domainName, int domain, Mod347 mod347,AsyncCallback<Void> callback);
	void saveMod347(String domainName, int domain, Mod347 mod347, AsyncCallback<Mod347> callback);
	void getMod347s(String domainName, int domain, AsyncCallback<LinkedList<Mod347>> callback);
	void getMod347(String domainName, int domain, Integer id, AsyncCallback<Mod347> callback);
//	void getMod347Detail(String domainName, int domain, Integer id, AsyncCallback<Mod347Detail> callback);
	void initializeMod347(String domainName, Integer domain, AsyncCallback<Mod347> callback);
	void saveCommentsMod347(String domainName, Mod347 mod347,AsyncCallback<Mod347> asyncCallback);
	void changeStatusMod347(String domainName, Mod347 mod347, FiscalStatus newStatus, AsyncCallback<Mod347> callback);
	void getInfo(String domainName, int domain, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback);

}
