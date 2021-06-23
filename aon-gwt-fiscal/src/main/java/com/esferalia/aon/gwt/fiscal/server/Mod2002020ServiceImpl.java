package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
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
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2020 Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Mod2002020" })
public class Mod2002020ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod2002020Service {

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
		HttpServletRequest request = getThreadLocalRequest();
		try {
			Mod2002019 mod2002019 = (Mod2002019) request.getSession().getAttribute("Mod2002019Import");
			if (mod2002019 != null) {
				if (!AonStringUtils.equals( mod2002019.getDocument(), mod200.getDocument())) {
					throw new AonCoreException("El NIF del documento importado no coincide");
				}
				Mod2002020Import2019.import2019(mod200, mod2002019);
				mod200.setInitializedFromLastYear(true);
			}
			return FISCAL.initializeMod2002020(domainName,domain,user,mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002019Import");
		}
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
	public Mod2002020 fillMod2002020AccountingData(Mod2002020 mod200)
			throws AonCoreException {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			MOD2002020 mod = (MOD2002020) request.getSession().getAttribute("Mod2002020Accounting");
			if (mod == null) {
				throw new AonCoreException("El fichero no se ha recibido correctamente");	
			}
			if (mod200 != null) {
				XMLtoMod2002020.fillMod2002020(mod, mod200);
			}
			return mod200; 
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002020Accounting");
		}
	}
	
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain, String user) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain, user);
	}
}
