package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model180ServiceAsync {
	
	void deleteMod180(String domainName, int domain, Mod180 mod180,AsyncCallback<Void> callback);
	void saveMod180(String domainName, int domain, Mod180 mod180, AsyncCallback<Mod180> callback);
	void getMod180s(String domainName, int domain, AsyncCallback<LinkedList<Mod180>> callback);
	void getMod180(String domainName, int domain, Integer id, AsyncCallback<Mod180> callback);
	void getMod180Detail(String domainName, int domain, Integer id, AsyncCallback<Mod180Detail> callback);
	void initializeMod180(String domainName, Integer domain, Integer year,AsyncCallback<Mod180> callback);
	void saveCommentsMod180(String domainName, Mod180 mod180,AsyncCallback<Mod180> asyncCallback);
	void changeStatusMod180(String domainName, Mod180 mod180, FiscalStatus newStatus, AsyncCallback<Mod180> callback);

}
