package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod2002021")
public interface Mod2002021Service extends RemoteService {
	
	// MODELO 200 - 2021	
	Mod2002021 getMod2002021ById(Occam occam, int id) throws AonCoreException;
	Mod2002021 createMod2002021(Occam occam, int year) throws AonCoreException;
	Mod2002021 initializeMod2002021(Occam occam, Mod2002021 mod200);
	Mod2002021 saveMod2002021(Occam occam, Mod2002021 mod200) throws AonCoreException;
	void deleteMod2002021(Occam occam, int id) throws AonCoreException;
	Mod2002021 fillMod2002021AccountingData(Occam occam, Mod2002021 mod200, String data);
	Mod2002021 calculateMod2002021(Mod2002021 mod200) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(Occam occam) throws AonCoreException;

}
