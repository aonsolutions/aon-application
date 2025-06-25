package com.esferalia.aon.gwt.mod200.client.mod200.e2024;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod2002024")
public interface Mod2002024Service extends RemoteService {
	
	// MODELO 200 - 2024	
	Mod2002024 getMod2002024ById(Occam occam, int id) throws AonCoreException;
	Mod2002024 createMod2002024(Occam occam, int year) throws AonCoreException;
	Mod2002024 initializeMod2002024(Occam occam, Mod2002024 mod200);
	Mod2002024 saveMod2002024(Occam occam, Mod2002024 mod200) throws AonCoreException;
	void deleteMod2002024(Occam occam, Mod2002024 mod200) throws AonCoreException;
	Mod2002024 fillMod2002024AccountingData(Occam occam, Mod2002024 mod200, String data) throws AonCoreException;
	Mod2002024 calculateMod2002024(Mod2002024 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(Occam occam) throws AonCoreException;

}
