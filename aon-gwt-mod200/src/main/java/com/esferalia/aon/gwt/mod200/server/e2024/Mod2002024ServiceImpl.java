package com.esferalia.aon.gwt.mod200.server.e2024;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2024.Mod2002024Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.MODEL2002024;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2024.jaxb.MOD2002024;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2024.jaxb.XMLtoMod2002024;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2024 Servlet", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002024" })
public class Mod2002024ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod2002024Service {
	
	@Override
	public Mod2002024 createMod2002024(Occam occam, int year)
			throws AonCoreException {
		return MODEL2002024.createMod2002024(occam,year);
	}

	@Override
	public Mod2002024 initializeMod2002024(Occam occam, Mod2002024 mod200) {
		return MODEL2002024.initializeMod2002024(occam,mod200);
	}

	@Override
	public Mod2002024 getMod2002024ById(Occam occam, int id)
			throws AonCoreException {
		return MODEL2002024.getMod2002024ById(occam,id);
	}

	@Override
	public Mod2002024 calculateMod2002024(Mod2002024 mod200) throws AonCoreException {
		return MODEL2002024.calculateMod2002024(mod200);
	}

	@Override
	public Mod2002024 saveMod2002024(Occam occam, Mod2002024 mod200)
			throws AonCoreException {
		return MODEL2002024.saveMod2002024(occam,mod200);
	}

	@Override
	public void deleteMod2002024(Occam occam, Mod2002024 mod200)
			throws AonCoreException {
		MODEL2002024.deleteMod2002024(occam, mod200);
	}

	@Override
	public Mod2002024 fillMod2002024AccountingData(Occam occam, Mod2002024 mod200, String base64) throws AonCoreException {
		byte[] fileData = Base64.getDecoder().decode(base64);
		ByteArrayInputStream input = new ByteArrayInputStream(fileData);
		try {
			JAXBContext context = JAXBContext.newInstance(MOD2002024.class);
			Unmarshaller um = context.createUnmarshaller();
			MOD2002024 mod = (MOD2002024) um.unmarshal(input);
			if (mod200 != null && mod != null) {
				XMLtoMod2002024.fillMod2002024(mod, mod200);
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

