package com.esferalia.aon.gwt.mod200.server.e2023;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Mod2002023Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.MODEL2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2023.jaxb.MOD2002023;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2023.jaxb.XMLtoMod2002023;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2023 Servlet", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002023" })
public class Mod2002023ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod2002023Service {
	
	@Override
	public Mod2002023 createMod2002023(Occam occam, int year)
			throws AonCoreException {
		return MODEL2002023.createMod2002023(occam,year);
	}

	@Override
	public Mod2002023 initializeMod2002023(Occam occam, Mod2002023 mod200) {
		return MODEL2002023.initializeMod2002023(occam,mod200);
	}

	@Override
	public Mod2002023 getMod2002023ById(Occam occam, int id)
			throws AonCoreException {
		return MODEL2002023.getMod2002023ById(occam,id);
	}

	@Override
	public Mod2002023 calculateMod2002023(Mod2002023 mod200) throws AonCoreException {
		return MODEL2002023.calculateMod2002023(mod200);
	}

	@Override
	public Mod2002023 saveMod2002023(Occam occam, Mod2002023 mod200)
			throws AonCoreException {
		return MODEL2002023.saveMod2002023(occam,mod200);
	}

	@Override
	public void deleteMod2002023(Occam occam, Mod2002023 mod200)
			throws AonCoreException {
		MODEL2002023.deleteMod2002023(occam, mod200);
	}

	@Override
	public Mod2002023 fillMod2002023AccountingData(Occam occam, Mod2002023 mod200, String base64) throws AonCoreException {
		byte[] fileData = Base64.getDecoder().decode(base64);
		ByteArrayInputStream input = new ByteArrayInputStream(fileData);
		try {
			JAXBContext context = JAXBContext.newInstance(MOD2002023.class);
			Unmarshaller um = context.createUnmarshaller();
			MOD2002023 mod = (MOD2002023) um.unmarshal(input);
			if (mod200 != null && mod != null) {
				XMLtoMod2002023.fillMod2002023(mod, mod200);
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
