package com.esferalia.aon.gwt.mod200.client.mod200.e2019;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002019ServiceAsync {

	void createMod2002019(String domainName, int domain, int year,AsyncCallback<Mod2002019> callback);
	void initializeNewMod2002019(String domainName, int domain, Mod2002019 mod200, AsyncCallback<Mod2002019> callback);
	void initializeMod2002019(String domainName, int domain, Mod2002019 mod200,AsyncCallback<Mod2002019> callback);
	void getMod2002019ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002019> callback);
	void calculateMod2002019(Mod2002019 mod200, AsyncCallback<Mod2002019> callback);
	void deleteMod2002019(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002019(Mod2002019 mod200, AsyncCallback<String> callback);
	void getMod2002019ById(String domainName, int domain, int id,AsyncCallback<Mod2002019> callback);
	void saveMod2002019(String domainName, int domain, Mod2002019 mod200,AsyncCallback<Mod2002019> callback);
	void validateMod2002019(Mod2002019 mod200, AsyncCallback<Mod2002019> callback);
	void importMod2002018(String domainName, int domain, Mod2002019 mod200,AsyncCallback<Mod2002019> callback);
	void fillMod2002019AccountingData(Mod2002019 mod200, AsyncCallback<Mod2002019> callback);
	void getCompanyBanks(String domainName, int domain, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
