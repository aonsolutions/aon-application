package com.esferalia.aon.gwt.mod200.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002020ServiceAsync {

	void createMod2002020(String domainName, int domain, String user, int year, AsyncCallback<Mod2002020> callback);
	void initializeNewMod2002020(String domainName, int domain, String user, Mod2002020 mod200, AsyncCallback<Mod2002020> callback);
	void initializeMod2002020(String domainName, int domain, String user, Mod2002020 mod200,AsyncCallback<Mod2002020> callback);
	void getMod2002020ByYear(String domainName, int domain, String user, int year,AsyncCallback<Mod2002020> callback);
	void calculateMod2002020(Mod2002020 mod200, AsyncCallback<Mod2002020> callback);
	void deleteMod2002020(String domainName, int domain, String user, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002020(Mod2002020 mod200, AsyncCallback<String> callback);
	void getMod2002020ById(String domainName, int domain, String user, int id,AsyncCallback<Mod2002020> callback);
	void saveMod2002020(String domainName, int domain, String user, Mod2002020 mod200,AsyncCallback<Mod2002020> callback);
	void validateMod2002020(Mod2002020 mod200, AsyncCallback<Mod2002020> callback);
	void importMod2002019(String domainName, int domain, String user, Mod2002020 mod200,AsyncCallback<Mod2002020> callback);
	void fillMod2002020AccountingData(String domainName, int domain, String user, Mod2002020 mod200, String data, AsyncCallback<Mod2002020> callback);
	void getCompanyBanks(String domainName, int domain, String user, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
