package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod2002021ServiceAsync {

	void createMod2002021(Occam occam, int year, AsyncCallback<Mod2002021> callback);
	void initializeMod2002021(Occam occam, Mod2002021 mod200,AsyncCallback<Mod2002021> callback);
	void calculateMod2002021(Mod2002021 mod200, AsyncCallback<Mod2002021> callback);
	void deleteMod2002021(Occam occam, int id,AsyncCallback<Void> callback);
	void getMod2002021ById(Occam occam, int id,AsyncCallback<Mod2002021> callback);
	void saveMod2002021(Occam occam, Mod2002021 mod200,AsyncCallback<Mod2002021> callback);
	void fillMod2002021AccountingData(Occam occam, Mod2002021 mod200, String data, AsyncCallback<Mod2002021> callback);
	void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> asyncCallback);

}
