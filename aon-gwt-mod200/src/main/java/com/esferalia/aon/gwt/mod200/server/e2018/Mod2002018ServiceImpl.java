package com.esferalia.aon.gwt.mod200.server.e2018;

import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.Mod2002018Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.FISCAL;
import com.esferalia.aon.occam.mod200.api.model.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2018.jaxb.MOD2002018;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2018.jaxb.XMLtoMod2002018;
import com.esferalia.aon.occam.mod200.server.format.Mod2002018Import2017;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2018 Servlet", urlPatterns = { "/aon_gwt_mod200/Mod2002018" })
public class Mod2002018ServiceImpl extends AonRemoteServiceServlet implements Mod2002018Service {

	@Override
	public Mod2002018 createMod2002018(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.createMod2002018(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002018 initializeNewMod2002018(String domainName, int domain, Mod2002018 mod200) {
		return FISCAL.initializeNewMod2002018(domainName,domain,this.getUserLogin(),mod200);
	}
	
	@Override
	public Mod2002018 initializeMod2002018(String domainName, int domain, Mod2002018 mod200) {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			Mod2002017 mod2002017 = (Mod2002017) request.getSession().getAttribute("Mod2002017Import");
			if (mod2002017 != null) {
				if (!AonStringUtils.equals( mod2002017.getDocument(), mod200.getDocument())) {
					throw new AonCoreException("El NIF del documento importado no coincide");
				}
				Mod2002018Import2017.import2017(mod200, mod2002017);
				mod200.setInitializedFromLastYear(true);
			}
			return FISCAL.initializeMod2002018(domainName,domain,this.getUserLogin(),mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002017Import");
		}
	}

	@Override
	public Mod2002018 getMod2002018ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002018ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002018 getMod2002018ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002018ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002018 calculateMod2002018(Mod2002018 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002018(mod200);
	}

	@Override
	public Mod2002018 saveMod2002018(String domainName, int domain, Mod2002018 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002018(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002018 validateMod2002018(Mod2002018 mod200) throws AonCoreException {
		return FISCAL.validateMod2002018(mod200);
	}

	@Override
	public void deleteMod2002018(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002018(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002018(Mod2002018 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002018(mod200);
	}

	@Override
	public Mod2002018 importMod2002017(String domainName, int domain,
			Mod2002018 mod200) throws AonCoreException {
		return FISCAL.importMod2002017(domainName, domain,this.getUserLogin(), mod200);
	}
	
	@Override
	public Mod2002018 fillMod2002018AccountingData(Mod2002018 mod200)
			throws AonCoreException {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			MOD2002018 mod = (MOD2002018) request.getSession().getAttribute("Mod2002018Accounting");
			if (mod == null) {
				throw new AonCoreException("El fichero no se ha recibido correctamente");	
			}
			if (mod200 != null) {
				XMLtoMod2002018.fillMod2002018(mod, mod200);
			}
			return mod200; 
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002018Accounting");
		}
	}
	
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,this.getUserLogin());
	}
}
