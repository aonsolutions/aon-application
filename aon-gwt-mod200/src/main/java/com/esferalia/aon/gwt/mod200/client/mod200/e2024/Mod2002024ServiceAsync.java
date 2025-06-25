package com.esferalia.aon.gwt.mod200.client.mod200.e2024;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002024ServiceAsync {

	void createMod2002024(Occam occam, int year, AsyncCallback<Mod2002024> callback);
	void initializeMod2002024(Occam occam, Mod2002024 mod200,AsyncCallback<Mod2002024> callback);
	void calculateMod2002024(Mod2002024 mod200, AsyncCallback<Mod2002024> callback);
	void deleteMod2002024(Occam occam, Mod2002024 mod200, AsyncCallback<Void> callback);
	void getMod2002024ById(Occam occam, int id,AsyncCallback<Mod2002024> callback);
	void saveMod2002024(Occam occam, Mod2002024 mod200,AsyncCallback<Mod2002024> callback);
	void fillMod2002024AccountingData(Occam occam, Mod2002024 mod200, String data, AsyncCallback<Mod2002024> callback);
	void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
