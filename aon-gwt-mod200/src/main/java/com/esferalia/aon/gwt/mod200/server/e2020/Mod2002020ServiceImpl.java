package com.esferalia.aon.gwt.mod200.server.e2020;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Mod2002020Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.MODEL2002020;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2020.jaxb.MOD2002020;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2020.jaxb.XMLtoMod2002020;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2020 Servlet", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002020" })
public class Mod2002020ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod2002020Service {
	
	@Override
	public Mod2002020 createMod2002020(Occam occam, int year)
			throws AonCoreException {
		return MODEL2002020.createMod2002020(occam,year);
	}

	@Override
	public Mod2002020 initializeNewMod2002020(Occam occam, Mod2002020 mod200) {
		return MODEL2002020.initializeNewMod2002020(occam,mod200);
	}
	
	@Override
	public Mod2002020 initializeMod2002020(Occam occam, Mod2002020 mod200) {
		return MODEL2002020.initializeMod2002020(occam,mod200);
	}

	@Override
	public Mod2002020 getMod2002020ByYear(Occam occam, int year)
			throws AonCoreException {
		return MODEL2002020.getMod2002020ByYear(occam,year);
	}

	@Override
	public Mod2002020 getMod2002020ById(Occam occam, int id)
			throws AonCoreException {
		return MODEL2002020.getMod2002020ById(occam,id);
	}

	@Override
	public Mod2002020 calculateMod2002020(Mod2002020 mod200) throws AonCoreException {
		return MODEL2002020.calculateMod2002020(mod200);
	}

	@Override
	public Mod2002020 saveMod2002020(Occam occam, Mod2002020 mod200)
			throws AonCoreException {
		return MODEL2002020.saveMod2002020(occam,mod200);
	}

//	@Override
//	public Mod2002020 validateMod2002020(Mod2002020 mod200) throws AonCoreException {
//		return MODEL2002020.validateMod2002020(mod200);
//	}

	@Override
	public void deleteMod2002020(Occam occam, int id)
			throws AonCoreException {
		MODEL2002020.deleteMod2002020(occam,id);
	}

	@Override
	public String dumpAEATMod2002020(Mod2002020 mod200) throws AonCoreException {
		return MODEL2002020.dumpAEATMod2002020(mod200);
	}

	@Override
	public Mod2002020 importMod2002019(Occam occam,
			Mod2002020 mod200) throws AonCoreException {
		return MODEL2002020.importMod2002019(occam, mod200);
	}
	
	@Override
	public Mod2002020 fillMod2002020AccountingData(Occam occam, Mod2002020 mod200, String base64) {
		byte[] fileData = Base64.getDecoder().decode(base64);
		ByteArrayInputStream input = new ByteArrayInputStream(fileData);
		try {
			JAXBContext context = JAXBContext.newInstance(MOD2002020.class);
			Unmarshaller um = context.createUnmarshaller();
			MOD2002020 mod = (MOD2002020) um.unmarshal(input);
			if (mod200 != null && mod != null) {
				XMLtoMod2002020.fillMod2002020(mod, mod200);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}		
		return mod200;
	}
	
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(Occam occam) throws AonCoreException {
		return AON.getCompanyBanks(occam.getDomainName(),occam.getDomain(), occam.getUser());
	}
}
