package com.esferalia.aon.gwt.mod200.server.e2016;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2016.Mod2002016Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.FISCAL;
import com.esferalia.aon.occam.mod200.api.model.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.mod200.api.model.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2015.jaxb.MOD2002015;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2015.jaxb.XMLtoMod2002015;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2016.jaxb.MOD2002016;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2016.jaxb.XMLtoMod2002016;
import com.esferalia.aon.occam.mod200.server.format.mod200_2014.Mod2002014Reader;
import com.esferalia.aon.occam.mod200.server.format.mod200_2015.Mod2002015Import2014;
import com.esferalia.aon.occam.mod200.server.format.mod200_2015.Mod2002015Reader;
import com.esferalia.aon.occam.mod200.server.format.mod200_2016.Mod2002016Import2015;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2016 Servlet", urlPatterns = { "/aon_gwt_mod200/Mod2002016" })
public class Mod2002016ServiceImpl extends AonRemoteServiceServlet implements Mod2002016Service {

	@Override
	public Mod2002016 createMod2002016(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.createMod2002016(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002016 initializeNewMod2002016(String domainName, int domain, Mod2002016 mod200) {
		return FISCAL.initializeNewMod2002016(domainName,domain,this.getUserLogin(),mod200);
	}
	
	@Override
	public Mod2002016 initializeMod2002016(String domainName, int domain, Mod2002016 mod200, String data) {
		try {
			if(data != null) {
				byte[] fileData = java.util.Base64.getDecoder().decode(data);
				ByteArrayInputStream input = new ByteArrayInputStream(fileData);
				Mod2002015 mod2002015 = Mod2002015Reader.getMod2002015(input);
			
				if (mod2002015 != null) {
					if (!AonStringUtils.equals( mod2002015.getDocument(), mod200.getDocument())) {
						throw new AonCoreException("El NIF del documento importado no coincide");
					}
					Mod2002016Import2015.import2015(mod200, mod2002015);
					mod200.setInitializedFromLastYear(true);
				}
			}
			return FISCAL.initializeMod2002016(domainName,domain,this.getUserLogin(),mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public Mod2002016 getMod2002016ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002016ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002016 getMod2002016ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002016ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002016 calculateMod2002016(Mod2002016 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002016(mod200);
	}

	@Override
	public Mod2002016 saveMod2002016(String domainName, int domain, Mod2002016 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002016(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002016 validateMod2002016(Mod2002016 mod200) throws AonCoreException {
		return FISCAL.validateMod2002016(mod200);
	}

	@Override
	public void deleteMod2002016(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002016(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002016(Mod2002016 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002016(mod200);
	}

	@Override
	public Mod2002016 importMod2002015(String domainName, int domain,
			Mod2002016 mod200) throws AonCoreException {
		return FISCAL.importMod2002015(domainName, domain,this.getUserLogin(), mod200);
	}
	
	@Override
	public Mod2002016 fillMod2002016AccountingData(Mod2002016 mod200, String data) throws AonCoreException {
		try {
			if(data != null) {
				byte[] fileData = java.util.Base64.getDecoder().decode(data);
				
				ByteArrayInputStream input = new ByteArrayInputStream(fileData);
				JAXBContext context = JAXBContext.newInstance(MOD2002016.class);
				Unmarshaller um = context.createUnmarshaller();
				MOD2002016 mod = (MOD2002016) um.unmarshal(input);
				
				if (mod200 != null) {
					XMLtoMod2002016.fillMod2002016(mod, mod200);
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
