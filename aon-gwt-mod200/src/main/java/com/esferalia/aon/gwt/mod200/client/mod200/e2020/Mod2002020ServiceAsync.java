package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002020ServiceAsync {

	void createMod2002020(Occam occam, int year, AsyncCallback<Mod2002020> callback);
	void initializeNewMod2002020(Occam occam, Mod2002020 mod200, AsyncCallback<Mod2002020> callback);
	void initializeMod2002020(Occam occam, Mod2002020 mod200,AsyncCallback<Mod2002020> callback);
	void getMod2002020ByYear(Occam occam, int year,AsyncCallback<Mod2002020> callback);
	void calculateMod2002020(Mod2002020 mod200, AsyncCallback<Mod2002020> callback);
	void deleteMod2002020(Occam occam, int id,AsyncCallback<Void> callback);
	void dumpAEATMod2002020(Mod2002020 mod200, AsyncCallback<String> callback);
	void getMod2002020ById(Occam occam, int id,AsyncCallback<Mod2002020> callback);
	void saveMod2002020(Occam occam, Mod2002020 mod200,AsyncCallback<Mod2002020> callback);
	void validateMod2002020(Mod2002020 mod200, AsyncCallback<Mod2002020> callback);
	void importMod2002019(Occam occam, Mod2002020 mod200,AsyncCallback<Mod2002020> callback);
	void fillMod2002020AccountingData(Occam occam, Mod2002020 mod200, String data, AsyncCallback<Mod2002020> callback);
	void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
