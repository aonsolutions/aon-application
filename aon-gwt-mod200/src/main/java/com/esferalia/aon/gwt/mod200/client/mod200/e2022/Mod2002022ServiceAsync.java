package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002022ServiceAsync {

	void createMod2002022(Occam occam, int year, AsyncCallback<Mod2002022> callback);
	void initializeMod2002022(Occam occam, Mod2002022 mod200,AsyncCallback<Mod2002022> callback);
	void calculateMod2002022(Mod2002022 mod200, AsyncCallback<Mod2002022> callback);
	void deleteMod2002022(Occam occam, Mod2002022 mod200, AsyncCallback<Void> callback);
	void getMod2002022ById(Occam occam, int id,AsyncCallback<Mod2002022> callback);
	void saveMod2002022(Occam occam, Mod2002022 mod200,AsyncCallback<Mod2002022> callback);
	void fillMod2002022AccountingData(Occam occam, Mod2002022 mod200, String data, AsyncCallback<Mod2002022> callback);
	void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
