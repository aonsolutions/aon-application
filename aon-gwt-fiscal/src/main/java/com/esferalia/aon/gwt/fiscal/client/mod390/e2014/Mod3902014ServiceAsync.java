package com.esferalia.aon.gwt.fiscal.client.mod390.e2014;

import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod3902014ServiceAsync {

	void getMod3902014(String domainName, Integer domain,String user,Mod390 mod390, AsyncCallback<Mod3902014> callback);
	void getById(String domainName, Integer domain,String user,Integer id, AsyncCallback<Mod3902014> callback);
	void saveMod3902014(String domainName, Integer domain,String user, Mod3902014 mod390, AsyncCallback<Mod3902014> callback);
	void deleteMod3902014(String domainName, Integer domain,String user, Mod3902014 mod390, AsyncCallback<Void> callback);

}
