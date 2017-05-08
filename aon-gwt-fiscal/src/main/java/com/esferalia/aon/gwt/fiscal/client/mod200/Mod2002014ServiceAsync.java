package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002014ServiceAsync {

	void createMod2002014(String domainName, int domain, int year,AsyncCallback<Mod2002014> callback);
	void initializeNewMod2002014(String domainName, int domain, Mod2002014 mod200, AsyncCallback<Mod2002014> callback);
	void initializeMod2002014(String domainName, int domain, Mod2002014 mod200,AsyncCallback<Mod2002014> callback);
	void getMod2002014ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002014> callback);
	void calculateMod2002014(Mod2002014 mod200, AsyncCallback<Mod2002014> callback);
	void deleteMod2002014(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002014(Mod2002014 mod200, AsyncCallback<String> callback);
	void getMod2002014ById(String domainName, int domain, int id,AsyncCallback<Mod2002014> callback);
	void saveMod2002014(String domainName, int domain, Mod2002014 mod200,AsyncCallback<Mod2002014> callback);
	void validateMod2002014(Mod2002014 mod200, AsyncCallback<Mod2002014> callback);
	void importMod2002013(String domainName, int domain, Mod2002014 mod200,AsyncCallback<Mod2002014> callback);
	void fillMod2002014AccountingData(Mod2002014 mod200, AsyncCallback<Mod2002014> callback);

	}
