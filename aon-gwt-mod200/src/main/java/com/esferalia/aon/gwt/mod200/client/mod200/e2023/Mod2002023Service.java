package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod2002023")
public interface Mod2002023Service extends RemoteService {
	
	// MODELO 200 - 2023	
	Mod2002023 getMod2002023ById(Occam occam, int id) throws AonCoreException;
	Mod2002023 createMod2002023(Occam occam, int year) throws AonCoreException;
	Mod2002023 initializeMod2002023(Occam occam, Mod2002023 mod200);
	Mod2002023 saveMod2002023(Occam occam, Mod2002023 mod200) throws AonCoreException;
	void deleteMod2002023(Occam occam, Mod2002023 mod200) throws AonCoreException;
	Mod2002023 fillMod2002023AccountingData(Occam occam, Mod2002023 mod200, String data) throws AonCoreException;
	Mod2002023 calculateMod2002023(Mod2002023 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(Occam occam) throws AonCoreException;

}
