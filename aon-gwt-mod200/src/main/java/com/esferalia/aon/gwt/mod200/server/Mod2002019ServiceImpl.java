package com.esferalia.aon.gwt.mod200.server;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.Mod2002019Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.FISCAL;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2019.jaxb.MOD2002019;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2019.jaxb.XMLtoMod2002019;
import com.esferalia.aon.occam.mod200.server.format.Mod2002019Import2018;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2019 Servlet", urlPatterns = { "/aon_gwt_mod200/Mod2002019" })
public class Mod2002019ServiceImpl extends AonRemoteServiceServlet implements Mod2002019Service {

	@Override
	public Mod2002019 createMod2002019(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.createMod2002019(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002019 initializeNewMod2002019(String domainName, int domain, Mod2002019 mod200) {
		return FISCAL.initializeNewMod2002019(domainName,domain,this.getUserLogin(),mod200);
	}
	
	@Override
	public Mod2002019 initializeMod2002019(String domainName, int domain, Mod2002019 mod200) {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			Mod2002018 mod2002018 = (Mod2002018) request.getSession().getAttribute("Mod2002018Import");
			if (mod2002018 != null) {
				if (!AonStringUtils.equals( mod2002018.getDocument(), mod200.getDocument())) {
					throw new AonCoreException("El NIF del documento importado no coincide");
				}
				Mod2002019Import2018.import2018(mod200, mod2002018);
				mod200.setInitializedFromLastYear(true);
			}
			return FISCAL.initializeMod2002019(domainName,domain,this.getUserLogin(),mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002018Import");
		}
	}

	@Override
	public Mod2002019 getMod2002019ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002019ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002019 getMod2002019ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002019ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002019 calculateMod2002019(Mod2002019 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002019(mod200);
	}

	@Override
	public Mod2002019 saveMod2002019(String domainName, int domain, Mod2002019 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002019(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002019 validateMod2002019(Mod2002019 mod200) throws AonCoreException {
		return FISCAL.validateMod2002019(mod200);
	}

	@Override
	public void deleteMod2002019(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002019(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002019(Mod2002019 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002019(mod200);
	}

	@Override
	public Mod2002019 importMod2002018(String domainName, int domain,
			Mod2002019 mod200) throws AonCoreException {
		return FISCAL.importMod2002018(domainName, domain,this.getUserLogin(), mod200);
	}
	
	@Override
	public Mod2002019 fillMod2002019AccountingData(Mod2002019 mod200)
			throws AonCoreException {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			MOD2002019 mod = (MOD2002019) request.getSession().getAttribute("Mod2002019Accounting");
			if (mod == null) {
				throw new AonCoreException("El fichero no se ha recibido correctamente");	
			}
			if (mod200 != null) {
				XMLtoMod2002019.fillMod2002019(mod, mod200);
			}
			return mod200; 
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002019Accounting");
		}
	}
	
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,this.getUserLogin());
	}
}
