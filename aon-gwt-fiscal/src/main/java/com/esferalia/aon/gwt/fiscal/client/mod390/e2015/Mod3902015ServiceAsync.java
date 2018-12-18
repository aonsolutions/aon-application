package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod3902015ServiceAsync {

	void getMod3902015(String domainName, Integer domain,String user,Mod390 mod390, AsyncCallback<Mod3902015> callback);
	void saveMod3902015(String domainName, Integer domain,String user, Mod3902015 mod390, AsyncCallback<Mod3902015> callback);
	void deleteMod3902015(String domainName, Integer domain,String user, Mod3902015 mod390, AsyncCallback<Void> callback);
	void changeStatus(String domainName,String user, Mod3902015 mod390, FiscalStatus status, AsyncCallback<Mod3902015> asyncCallback);
	void presentationFile(String domainName, Integer domainId,String user, Integer id, AsyncCallback<Integer> callback);

}
