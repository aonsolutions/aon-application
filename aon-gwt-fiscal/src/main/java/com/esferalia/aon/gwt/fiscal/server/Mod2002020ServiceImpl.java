package com.esferalia.aon.gwt.fiscal.server;

import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.common.shared.Base64;
import com.esferalia.aon.gwt.fiscal.client.mod200.Mod2002020Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2020.jaxb.MOD2002020;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2020.jaxb.XMLtoMod2002020;
import com.esferalia.aon.occam.server.fiscal.format.Mod2002020Import2019;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2020 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod2002020" })
public class Mod2002020ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod2002020Service {

	public static HashMap<String, MOD2002020> modImport;
	
	public static HashMap<String, MOD2002020> getModImport() {
		return modImport;
	}
	
	public static void addModImport(String hashId, MOD2002020 data) {
		if(modImport == null) {
			modImport = new HashMap<String, MOD2002020>();
		}
		modImport.put(hashId, data);
	}
	
	@Override
	public Mod2002020 createMod2002020(String domainName, int domain, String user, int year)
			throws AonCoreException {
		return FISCAL.createMod2002020(domainName,domain,user,year);
	}

	@Override
	public Mod2002020 initializeNewMod2002020(String domainName, int domain, String user, Mod2002020 mod200) {
		return FISCAL.initializeNewMod2002020(domainName,domain,user,mod200);
	}
	
	@Override
	public Mod2002020 initializeMod2002020(String domainName, int domain, String user, Mod2002020 mod200) {
		// Esto no hace falta, porque cuando llega aqui ya están copiados los datos del ejercicio anterior, se copia en el createNewMod200 del DAO
//		Mod2002019 mod2002019 = FISCAL.getMod2002019ByYear(domainName, domain, user, 2019);
//		Mod2002020Import2019.import2019(mod200, mod2002019);
//		mod200.setInitializedFromLastYear(true);
		return FISCAL.initializeMod2002020(domainName,domain,user,mod200);
	}

	@Override
	public Mod2002020 getMod2002020ByYear(String domainName, int domain, String user, int year)
			throws AonCoreException {
		return FISCAL.getMod2002020ByYear(domainName,domain,user,year);
	}

	@Override
	public Mod2002020 getMod2002020ById(String domainName, int domain, String user, int id)
			throws AonCoreException {
		return FISCAL.getMod2002020ById(domainName,domain,user,id);
	}

	@Override
	public Mod2002020 calculateMod2002020(Mod2002020 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002020(mod200);
	}

	@Override
	public Mod2002020 saveMod2002020(String domainName, int domain, String user, Mod2002020 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002020(domainName,domain,user,mod200);
	}

	@Override
	public Mod2002020 validateMod2002020(Mod2002020 mod200) throws AonCoreException {
		return FISCAL.validateMod2002020(mod200);
	}

	@Override
	public void deleteMod2002020(String domainName, int domain, String user, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002020(domainName,domain,user,id);
	}

	@Override
	public String dumpAEATMod2002020(Mod2002020 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002020(mod200);
	}

	@Override
	public Mod2002020 importMod2002019(String domainName, int domain, String user,
			Mod2002020 mod200) throws AonCoreException {
		return FISCAL.importMod2002019(domainName, domain, user, mod200);
	}
	
	@Override
	public Mod2002020 fillMod2002020AccountingData(String domainName, int domain, String user, Mod2002020 mod200) {
		String hashId = Base64.encode(domainName + user);		
		MOD2002020 mod = getModImport().get(hashId);
		if (mod200 != null) {
			XMLtoMod2002020.fillMod2002020(mod, mod200);
		}
		return mod200;
	}
	
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain, String user) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain, user);
	}
}
