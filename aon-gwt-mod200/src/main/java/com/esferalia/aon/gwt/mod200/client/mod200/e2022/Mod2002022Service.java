package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod2002022")
public interface Mod2002022Service extends RemoteService {
	
	// MODELO 200 - 2022	
	Mod2002022 getMod2002022ById(Occam occam, int id) throws AonCoreException;
	Mod2002022 createMod2002022(Occam occam, int year) throws AonCoreException;
	Mod2002022 initializeMod2002022(Occam occam, Mod2002022 mod200);
	Mod2002022 saveMod2002022(Occam occam, Mod2002022 mod200) throws AonCoreException;
	void deleteMod2002022(Occam occam, Mod2002022 mod200) throws AonCoreException;
	Mod2002022 fillMod2002022AccountingData(Occam occam, Mod2002022 mod200, String data);
	Mod2002022 calculateMod2002022(Mod2002022 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(Occam occam) throws AonCoreException;

}
