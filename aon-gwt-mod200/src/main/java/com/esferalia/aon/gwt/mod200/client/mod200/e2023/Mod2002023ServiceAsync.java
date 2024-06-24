package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002023ServiceAsync {

	void createMod2002023(Occam occam, int year, AsyncCallback<Mod2002023> callback);
	void initializeMod2002023(Occam occam, Mod2002023 mod200,AsyncCallback<Mod2002023> callback);
	void calculateMod2002023(Mod2002023 mod200, AsyncCallback<Mod2002023> callback);
	void deleteMod2002023(Occam occam, Mod2002023 mod200, AsyncCallback<Void> callback);
	void getMod2002023ById(Occam occam, int id,AsyncCallback<Mod2002023> callback);
	void saveMod2002023(Occam occam, Mod2002023 mod200,AsyncCallback<Mod2002023> callback);
	void fillMod2002023AccountingData(Occam occam, Mod2002023 mod200, String data, AsyncCallback<Mod2002023> callback);
	void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
