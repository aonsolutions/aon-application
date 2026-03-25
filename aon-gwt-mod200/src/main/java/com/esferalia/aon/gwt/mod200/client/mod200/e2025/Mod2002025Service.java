package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod2002025")
public interface Mod2002025Service extends RemoteService {
	
	// MODELO 200 - 2025	
	Mod2002025 getMod2002025ById(Occam occam, int id) throws AonCoreException;
	Mod2002025 createMod2002025(Occam occam, int year) throws AonCoreException;
	Mod2002025 initializeMod2002025(Occam occam, Mod2002025 mod200);
	Mod2002025 saveMod2002025(Occam occam, Mod2002025 mod200) throws AonCoreException;
	void deleteMod2002025(Occam occam, Mod2002025 mod200) throws AonCoreException;
	Mod2002025 fillMod2002025AccountingData(Occam occam, Mod2002025 mod200, String data) throws AonCoreException;
	Mod2002025 calculateMod2002025(Mod2002025 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(Occam occam) throws AonCoreException;

}
