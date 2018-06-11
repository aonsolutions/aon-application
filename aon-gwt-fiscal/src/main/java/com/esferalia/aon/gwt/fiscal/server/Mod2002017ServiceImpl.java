package com.esferalia.aon.gwt.fiscal.server;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.mod200.Mod2002017Service;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2017.jaxb.MOD2002017;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2017.jaxb.XMLtoMod2002017;
import com.esferalia.aon.occam.server.fiscal.format.Mod2002017Import2016;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2017 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod2002017" })
public class Mod2002017ServiceImpl extends AonRemoteServiceServlet implements Mod2002017Service {

	@Override
	public Mod2002017 createMod2002017(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.createMod2002017(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002017 initializeNewMod2002017(String domainName, int domain, Mod2002017 mod200) {
		return FISCAL.initializeNewMod2002017(domainName,domain,this.getUserLogin(),mod200);
	}
	
	@Override
	public Mod2002017 initializeMod2002017(String domainName, int domain, Mod2002017 mod200) {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			Mod2002016 mod2002016 = (Mod2002016) request.getSession().getAttribute("Mod2002016Import");
			if (mod2002016 != null) {
				if (!AonStringUtils.equals( mod2002016.getDocument(), mod200.getDocument())) {
					throw new AonCoreException("El NIF del documento importado no coincide");
				}
				Mod2002017Import2016.import2016(mod200, mod2002016);
				mod200.setInitializedFromLastYear(true);
			}
			return FISCAL.initializeMod2002017(domainName,domain,this.getUserLogin(),mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002016Import");
		}
	}

	@Override
	public Mod2002017 getMod2002017ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002017ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002017 getMod2002017ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002017ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002017 calculateMod2002017(Mod2002017 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002017(mod200);
	}

	@Override
	public Mod2002017 saveMod2002017(String domainName, int domain, Mod2002017 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002017(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002017 validateMod2002017(Mod2002017 mod200) throws AonCoreException {
		return FISCAL.validateMod2002017(mod200);
	}

	@Override
	public void deleteMod2002017(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002017(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002017(Mod2002017 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002017(mod200);
	}

	@Override
	public Mod2002017 importMod2002016(String domainName, int domain,
			Mod2002017 mod200) throws AonCoreException {
		return FISCAL.importMod2002016(domainName, domain,this.getUserLogin(), mod200);
	}
	
	@Override
	public Mod2002017 fillMod2002017AccountingData(Mod2002017 mod200)
			throws AonCoreException {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			MOD2002017 mod = (MOD2002017) request.getSession().getAttribute("Mod2002017Accounting");
			if (mod == null) {
				throw new AonCoreException("El fichero no se ha recibido correctamente");	
			}
			if (mod200 != null) {
				XMLtoMod2002017.fillMod2002017(mod, mod200);
			}
			return mod200; 
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002017Accounting");
		}
	}
}
