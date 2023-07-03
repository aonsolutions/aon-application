package com.esferalia.aon.gwt.mod200.client.mod200.e2018;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002018ServiceAsync {

	void createMod2002018(String domainName, int domain, int year,AsyncCallback<Mod2002018> callback);
	void initializeNewMod2002018(String domainName, int domain, Mod2002018 mod200, AsyncCallback<Mod2002018> callback);
	void initializeMod2002018(String domainName, int domain, Mod2002018 mod200,AsyncCallback<Mod2002018> callback);
	void getMod2002018ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002018> callback);
	void calculateMod2002018(Mod2002018 mod200, AsyncCallback<Mod2002018> callback);
	void deleteMod2002018(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002018(Mod2002018 mod200, AsyncCallback<String> callback);
	void getMod2002018ById(String domainName, int domain, int id,AsyncCallback<Mod2002018> callback);
	void saveMod2002018(String domainName, int domain, Mod2002018 mod200,AsyncCallback<Mod2002018> callback);
	void validateMod2002018(Mod2002018 mod200, AsyncCallback<Mod2002018> callback);
	void importMod2002017(String domainName, int domain, Mod2002018 mod200,AsyncCallback<Mod2002018> callback);
	void fillMod2002018AccountingData(Mod2002018 mod200, AsyncCallback<Mod2002018> callback);
	void getCompanyBanks(String domainName, int domain, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
