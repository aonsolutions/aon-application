package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002015ServiceAsync {
	
	void createMod2002015(String domainName, int domain, int year,AsyncCallback<Mod2002015> callback);
	void initializeNewMod2002015(String domainName, int domain, Mod2002015 mod200, AsyncCallback<Mod2002015> callback);
	void initializeMod2002015(String domainName, int domain, Mod2002015 mod200,AsyncCallback<Mod2002015> callback);
	void getMod2002015ByYear(String domainName, int domain, int year,AsyncCallback<Mod2002015> callback);
	void calculateMod2002015(Mod2002015 mod200, AsyncCallback<Mod2002015> callback);
	void deleteMod2002015(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002015(Mod2002015 mod200, AsyncCallback<String> callback);
	void getMod2002015ById(String domainName, int domain, int id,AsyncCallback<Mod2002015> callback);
	void saveMod2002015(String domainName, int domain, Mod2002015 mod200,AsyncCallback<Mod2002015> callback);
	void validateMod2002015(Mod2002015 mod200, AsyncCallback<Mod2002015> callback);
	void importMod2002014(String domainName, int domain, Mod2002015 mod200,AsyncCallback<Mod2002015> callback);
	void fillMod2002015AccountingData(Mod2002015 mod200, AsyncCallback<Mod2002015> callback);

}
