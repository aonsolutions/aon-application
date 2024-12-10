package com.esferalia.aon.gwt.mod200.server.e2017;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2017.Mod2002017Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.FISCAL;
import com.esferalia.aon.occam.mod200.api.model.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.mod200.api.model.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2016.jaxb.MOD2002016;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2016.jaxb.XMLtoMod2002016;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2017.jaxb.MOD2002017;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2017.jaxb.XMLtoMod2002017;
import com.esferalia.aon.occam.mod200.server.format.mod200_2015.Mod2002015Reader;
import com.esferalia.aon.occam.mod200.server.format.mod200_2016.Mod2002016Import2015;
import com.esferalia.aon.occam.mod200.server.format.mod200_2017.Mod2002017Import2016;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2017 Servlet", urlPatterns = { "/aon_gwt_mod200/Mod2002017" })
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
			return FISCAL.initializeMod2002017(domainName,domain,this.getUserLogin(), mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
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
	public Mod2002017 fillMod2002017AccountingData(Mod2002017 mod200, String data) throws AonCoreException {
		try {
			if(data != null) {
				byte[] fileData = java.util.Base64.getDecoder().decode(data);
				
				ByteArrayInputStream input = new ByteArrayInputStream(fileData);
				JAXBContext context = JAXBContext.newInstance(MOD2002017.class);
				Unmarshaller um = context.createUnmarshaller();
				MOD2002017 mod = (MOD2002017) um.unmarshal(input);
				
				if (mod200 != null) {
					XMLtoMod2002017.fillMod2002017(mod, mod200);
				}				
			}
			return mod200; 
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName, int domain) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,this.getUserLogin());
	}
}
