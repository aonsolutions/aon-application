package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002025ServiceAsync {

	void createMod2002025(Occam occam, int year, AsyncCallback<Mod2002025> callback);
	void initializeMod2002025(Occam occam, Mod2002025 mod200,AsyncCallback<Mod2002025> callback);
	void calculateMod2002025(Mod2002025 mod200, AsyncCallback<Mod2002025> callback);
	void deleteMod2002025(Occam occam, Mod2002025 mod200, AsyncCallback<Void> callback);
	void getMod2002025ById(Occam occam, int id,AsyncCallback<Mod2002025> callback);
	void saveMod2002025(Occam occam, Mod2002025 mod200,AsyncCallback<Mod2002025> callback);
	void fillMod2002025AccountingData(Occam occam, Mod2002025 mod200, String data, AsyncCallback<Mod2002025> callback);
	void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
