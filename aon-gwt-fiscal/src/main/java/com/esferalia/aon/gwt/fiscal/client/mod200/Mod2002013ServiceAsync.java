package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002013ServiceAsync {

	void createMod2002013(String domainName, int domain, int year,AsyncCallback<Mod2002013> callback);
	void initializeNewMod2002013(String domainName, int domain, Mod2002013 mod200, AsyncCallback<Mod2002013> callback);
	void initializeMod2002013(String domainName, int domain, Mod2002013 mod200,AsyncCallback<Mod2002013> callback);
	void getMod2002013ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002013> callback);
	void calculateMod2002013(Mod2002013 mod200, AsyncCallback<Mod2002013> callback);
	void deleteMod2002013(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002013(Mod2002013 mod200, AsyncCallback<String> callback);
	void getMod2002013ById(String domainName, int domain, int id,AsyncCallback<Mod2002013> callback);
	void saveMod2002013(String domainName, int domain, Mod2002013 mod200,AsyncCallback<Mod2002013> callback);
	void validateMod2002013(Mod2002013 mod200, AsyncCallback<Mod2002013> callback);
	
}
