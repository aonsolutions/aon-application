package com.esferalia.aon.gwt.mod200.client.mod200.e2016;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002016ServiceAsync {

	void createMod2002016(String domainName, int domain, int year,AsyncCallback<Mod2002016> callback);
	void initializeNewMod2002016(String domainName, int domain, Mod2002016 mod200, AsyncCallback<Mod2002016> callback);
	void initializeMod2002016(String domainName, int domain, Mod2002016 mod200, String data, AsyncCallback<Mod2002016> callback);
	void getMod2002016ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002016> callback);
	void calculateMod2002016(Mod2002016 mod200, AsyncCallback<Mod2002016> callback);
	void deleteMod2002016(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002016(Mod2002016 mod200, AsyncCallback<String> callback);
	void getMod2002016ById(String domainName, int domain, int id,AsyncCallback<Mod2002016> callback);
	void saveMod2002016(String domainName, int domain, Mod2002016 mod200,AsyncCallback<Mod2002016> callback);
	void validateMod2002016(Mod2002016 mod200, AsyncCallback<Mod2002016> callback);
	void importMod2002015(String domainName, int domain, Mod2002016 mod200,AsyncCallback<Mod2002016> callback);
	void fillMod2002016AccountingData(Mod2002016 mod200, String data, AsyncCallback<Mod2002016> callback);
	void getCompanyBanks(String domainName, int domain, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
