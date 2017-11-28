package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model349ServiceAsync {
	
	// ---------------------------------------------------------------MODELO 349
	void deleteMod349(String domainName, int domain, Mod349 mod349,AsyncCallback<Void> callback);
	void saveMod349(String domainName, int domain, Mod349 mod349, AsyncCallback<Mod349> callback);
	void getMod349s(String domainName, int domain, AsyncCallback<LinkedList<Mod349>> callback);
	void getMod349(String domainName, int domain, Integer id, AsyncCallback<Mod349> callback);
	void getMod349Detail(String domainName, int domain, Integer id, AsyncCallback<Mod349Detail> callback);
	void initializeMod349(String domainName, Integer domain, AsyncCallback<Mod349> callback);
	void saveCommentsMod349(String domainName, Mod349 mod349,AsyncCallback<Mod349> asyncCallback);
	void changeStatusMod349(String domainName, Mod349 mod349, FiscalStatus newStatus, AsyncCallback<Mod349> callback);
	void getInfo(String domainName, int domain, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback);

}
