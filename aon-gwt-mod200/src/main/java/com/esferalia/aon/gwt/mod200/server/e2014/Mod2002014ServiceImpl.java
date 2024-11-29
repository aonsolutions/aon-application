package com.esferalia.aon.gwt.mod200.server.e2014;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2014.Mod2002014Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.FISCAL;
import com.esferalia.aon.occam.mod200.api.model.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.mod200.api.model.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2014.jaxb.MOD2002014;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2014.jaxb.XMLtoMod2002014;
import com.esferalia.aon.occam.mod200.server.format.Mod2002013Reader;
import com.esferalia.aon.occam.mod200.server.format.mod200_2014.Mod2002014Import2013;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@SuppressWarnings("serial")
@WebServlet(name = "Mod 200 2014 Servlet", urlPatterns = { "/aon_gwt_mod200/Mod2002014" })
public class Mod2002014ServiceImpl extends AonRemoteServiceServlet implements Mod2002014Service {

	@Override
	public Mod2002014 createMod2002014(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.createMod2002014(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002014 initializeNewMod2002014(String domainName, int domain, Mod2002014 mod200) {
		return FISCAL.initializeNewMod2002014(domainName,domain,this.getUserLogin(),mod200);
	}
	
	@Override
	public Mod2002014 initializeMod2002014(String domainName, int domain, Mod2002014 mod200, String data) {
		try {
			if(data != null) {
				byte[] fileData = java.util.Base64.getDecoder().decode(data);
				ByteArrayInputStream input = new ByteArrayInputStream(fileData);
				Mod2002013 mod2002013 = Mod2002013Reader.getMod2002013(input);
				
				if (mod2002013 != null) {
					if (!AonStringUtils.equals( mod2002013.getDocument(), mod200.getDocument())) {
						throw new AonCoreException("El NIF del documento importado no coincide");
					}
					Mod2002014Import2013.import2013(mod200, mod2002013);
					mod200.setInitializedFromLastYear(true);
				}				
			}

			return FISCAL.initializeMod2002014(domainName,domain,this.getUserLogin(),mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public Mod2002014 getMod2002014ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002014ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002014 getMod2002014ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002014ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002014 calculateMod2002014(Mod2002014 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002014(mod200);
	}

	@Override
	public Mod2002014 saveMod2002014(String domainName, int domain, Mod2002014 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002014(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002014 validateMod2002014(Mod2002014 mod200) throws AonCoreException {
		return FISCAL.validateMod2002014(mod200);
	}

	@Override
	public void deleteMod2002014(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002014(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002014(Mod2002014 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002014(mod200);
	}

	@Override
	public Mod2002014 importMod2002013(String domainName, int domain,
			Mod2002014 mod200) throws AonCoreException {
		return FISCAL.importMod2002013(domainName, domain,this.getUserLogin(), mod200);
	}
	
	@Override
	public Mod2002014 fillMod2002014AccountingData(Mod2002014 mod200, String data) throws AonCoreException {
		try {
			if(data != null) {
				byte[] fileData = java.util.Base64.getDecoder().decode(data);
				
				ByteArrayInputStream input = new ByteArrayInputStream(fileData);
				JAXBContext context = JAXBContext.newInstance(MOD2002014.class);
				Unmarshaller um = context.createUnmarshaller();
				MOD2002014 mod = (MOD2002014) um.unmarshal(input);
				
				if (mod200 != null) {
					XMLtoMod2002014.fillMod2002014(mod, mod200);
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
