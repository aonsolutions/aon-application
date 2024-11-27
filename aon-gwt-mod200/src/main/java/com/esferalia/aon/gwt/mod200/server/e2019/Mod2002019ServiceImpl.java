package com.esferalia.aon.gwt.mod200.server.e2019;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2019.Mod2002019Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.FISCAL;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2017.jaxb.MOD2002017;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2017.jaxb.XMLtoMod2002017;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2019.jaxb.MOD2002019;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2019.jaxb.XMLtoMod2002019;
import com.esferalia.aon.occam.mod200.server.format.mod200_2019.Mod2002019Import2018;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

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
		try {
			return FISCAL.initializeMod2002019(domainName,domain,this.getUserLogin(),mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
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
	public Mod2002019 fillMod2002019AccountingData(Mod2002019 mod200, String data) throws AonCoreException {
		try {
			if(data != null) {
				byte[] fileData = java.util.Base64.getDecoder().decode(data);
				
				ByteArrayInputStream input = new ByteArrayInputStream(fileData);
				JAXBContext context = JAXBContext.newInstance(MOD2002019.class);
				Unmarshaller um = context.createUnmarshaller();
				MOD2002019 mod = (MOD2002019) um.unmarshal(input);
				
				if (mod200 != null) {
					XMLtoMod2002019.fillMod2002019(mod, mod200);
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
