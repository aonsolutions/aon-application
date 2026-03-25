package com.esferalia.aon.gwt.mod200.server.e2025;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2025.Mod2002025Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.MODEL2002025;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2025.jaxb.MOD2002025;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2025.jaxb.XMLtoMod2002025;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2025 Servlet", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002025" })
public class Mod2002025ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod2002025Service {
	
	@Override
	public Mod2002025 createMod2002025(Occam occam, int year)
			throws AonCoreException {
		return MODEL2002025.createMod2002025(occam,year);
	}

	@Override
	public Mod2002025 initializeMod2002025(Occam occam, Mod2002025 mod200) {
		return MODEL2002025.initializeMod2002025(occam,mod200);
	}

	@Override
	public Mod2002025 getMod2002025ById(Occam occam, int id)
			throws AonCoreException {
		return MODEL2002025.getMod2002025ById(occam,id);
	}

	@Override
	public Mod2002025 calculateMod2002025(Mod2002025 mod200) throws AonCoreException {
		return MODEL2002025.calculateMod2002025(mod200);
	}

	@Override
	public Mod2002025 saveMod2002025(Occam occam, Mod2002025 mod200)
			throws AonCoreException {
		return MODEL2002025.saveMod2002025(occam,mod200);
	}

	@Override
	public void deleteMod2002025(Occam occam, Mod2002025 mod200)
			throws AonCoreException {
		MODEL2002025.deleteMod2002025(occam, mod200);
	}

	@Override
	public Mod2002025 fillMod2002025AccountingData(Occam occam, Mod2002025 mod200, String base64) throws AonCoreException {
		byte[] fileData = Base64.getDecoder().decode(base64);
		ByteArrayInputStream input = new ByteArrayInputStream(fileData);
		try {
			JAXBContext context = JAXBContext.newInstance(MOD2002025.class);
			Unmarshaller um = context.createUnmarshaller();
			MOD2002025 mod = (MOD2002025) um.unmarshal(input);
			if (mod200 != null && mod != null) {
				XMLtoMod2002025.fillMod2002025(mod, mod200);
			}
		} catch (JAXBException e) {
			throw new AonCoreException(e);
		}		
		return mod200;
	}
	
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(Occam occam) throws AonCoreException {
		return AON.getCompanyBanks(occam.getDomainName(),occam.getDomain(), occam.getUser());
	}
}

