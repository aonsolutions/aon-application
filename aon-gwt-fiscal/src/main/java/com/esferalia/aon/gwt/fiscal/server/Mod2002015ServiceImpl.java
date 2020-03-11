package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod200.Mod2002015Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2015.jaxb.MOD2002015;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2015.jaxb.XMLtoMod2002015;
import com.esferalia.aon.occam.server.fiscal.format.Mod2002015Import2014;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod 200 2015 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod2002015" })
public class Mod2002015ServiceImpl extends AonRemoteServiceServlet implements Mod2002015Service {

	@Override
	public Mod2002015 createMod2002015(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.createMod2002015(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002015 initializeNewMod2002015(String domainName, int domain, Mod2002015 mod200) {
		return FISCAL.initializeNewMod2002015(domainName,domain,this.getUserLogin(),mod200);
	}
	
	@Override
	public Mod2002015 initializeMod2002015(String domainName, int domain, Mod2002015 mod200) {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			Mod2002014 mod2002014 = (Mod2002014) request.getSession().getAttribute("Mod2002014Import");
			if (mod2002014 != null) {
				if (!AonStringUtils.equals( mod2002014.getDocument(), mod200.getDocument())) {
					throw new AonCoreException("El NIF del documento importado no coincide");
				}
				Mod2002015Import2014.import2014(mod200, mod2002014);
				mod200.setInitializedFromLastYear(true);
			}
			return FISCAL.initializeMod2002015(domainName,domain,this.getUserLogin(),mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002013Import");
		}
	}

	@Override
	public Mod2002015 getMod2002015ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002015ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002015 getMod2002015ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002015ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002015 calculateMod2002015(Mod2002015 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002015(mod200);
	}

	@Override
	public Mod2002015 saveMod2002015(String domainName, int domain, Mod2002015 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002015(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002015 validateMod2002015(Mod2002015 mod200) throws AonCoreException {
		return FISCAL.validateMod2002015(mod200);
	}

	@Override
	public void deleteMod2002015(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002015(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002015(Mod2002015 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002015(mod200);
	}

	@Override
	public Mod2002015 importMod2002014(String domainName, int domain,
			Mod2002015 mod200) throws AonCoreException {
		return FISCAL.importMod2002014(domainName, domain,this.getUserLogin(), mod200);
	}
	
	@Override
	public Mod2002015 fillMod2002015AccountingData(Mod2002015 mod200)
			throws AonCoreException {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			MOD2002015 mod = (MOD2002015) request.getSession().getAttribute("Mod2002015Accounting");
			if (mod200 != null) {
				XMLtoMod2002015.fillMod2002015(mod, mod200);
			}
			return mod200; 
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002015Accounting");
		}
	}
	
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,this.getUserLogin());
	}
}
