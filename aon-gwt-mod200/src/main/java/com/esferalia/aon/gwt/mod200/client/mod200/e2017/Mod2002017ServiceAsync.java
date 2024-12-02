package com.esferalia.aon.gwt.mod200.client.mod200.e2017;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2017.Mod2002017;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002017ServiceAsync {

	void createMod2002017(String domainName, int domain, int year,AsyncCallback<Mod2002017> callback);
	void initializeNewMod2002017(String domainName, int domain, Mod2002017 mod200, AsyncCallback<Mod2002017> callback);
	void initializeMod2002017(String domainName, int domain, Mod2002017 mod200,AsyncCallback<Mod2002017> callback);
	void getMod2002017ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002017> callback);
	void calculateMod2002017(Mod2002017 mod200, AsyncCallback<Mod2002017> callback);
	void deleteMod2002017(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002017(Mod2002017 mod200, AsyncCallback<String> callback);
	void getMod2002017ById(String domainName, int domain, int id,AsyncCallback<Mod2002017> callback);
	void saveMod2002017(String domainName, int domain, Mod2002017 mod200,AsyncCallback<Mod2002017> callback);
	void validateMod2002017(Mod2002017 mod200, AsyncCallback<Mod2002017> callback);
	void importMod2002016(String domainName, int domain, Mod2002017 mod200,AsyncCallback<Mod2002017> callback);
	void fillMod2002017AccountingData(Mod2002017 mod200, String data, AsyncCallback<Mod2002017> callback);
	void getCompanyBanks(String domainName, int domain, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
