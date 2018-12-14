package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod3902018ServiceAsync {

	void getMod3902018(String domainName, Integer domain,Mod390 mod390, AsyncCallback<Mod3902018> callback);
	void saveMod3902018(String domainName, Integer domain, Mod3902018 mod390, AsyncCallback<Mod3902018> callback);
	void deleteMod3902018(String domainName, Integer domain, Mod3902018 mod390, AsyncCallback<Void> callback);
	void changeStatus(String domainName, Mod3902018 mod390, FiscalStatus status, AsyncCallback<Mod3902018> asyncCallback);
	void presentationFile(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Integer> callback);

}
