package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod200.Mod2002013Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod 200 2013 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod2002013" })
public class Mod2002013ServiceImpl extends AonRemoteServiceServlet implements Mod2002013Service {

	@Override
	public Mod2002013 createMod2002013(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.createMod2002013(domainName,domain,this.getUserLogin(),year);
	}
	@Override
	public Mod2002013 initializeNewMod2002013(String domainName, int domain, Mod2002013 mod200) {
		return FISCAL.initializeNewMod2002013(domainName,domain,this.getUserLogin(),mod200);
	}
	
	@Override
	public Mod2002013 initializeMod2002013(String domainName, int domain, Mod2002013 mod200) {
		return FISCAL.initializeMod2002013(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002013 getMod2002013ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002013ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002013 getMod2002013ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002013ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002013 calculateMod2002013(Mod2002013 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002013(mod200);
	}

	@Override
	public Mod2002013 saveMod2002013(String domainName, int domain, Mod2002013 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002013(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002013 validateMod2002013(Mod2002013 mod200) throws AonCoreException {
		return FISCAL.validateMod2002013(mod200);
	}

	@Override
	public void deleteMod2002013(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002013(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002013(Mod2002013 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002013(mod200);
	}
	
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,this.getUserLogin());
	}
	
}
