package com.esferalia.aon.gwt.mod200.server.e2021;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Mod2002021Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.MODEL2002021;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2021.jaxb.MOD2002021;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2021.jaxb.XMLtoMod2002021;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2021 Servlet", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002021" })
public class Mod2002021ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod2002021Service {
	
	@Override
	public Mod2002021 createMod2002021(Occam occam, int year)
			throws AonCoreException {
		return MODEL2002021.createMod2002021(occam,year);
	}

	@Override
	public Mod2002021 initializeMod2002021(Occam occam, Mod2002021 mod200) {
		return MODEL2002021.initializeMod2002021(occam,mod200);
	}

	@Override
	public Mod2002021 getMod2002021ById(Occam occam, int id)
			throws AonCoreException {
		return MODEL2002021.getMod2002021ById(occam,id);
	}

	@Override
	public Mod2002021 calculateMod2002021(Mod2002021 mod200) throws AonCoreException {
		return MODEL2002021.calculateMod2002021(mod200);
	}

	@Override
	public Mod2002021 saveMod2002021(Occam occam, Mod2002021 mod200)
			throws AonCoreException {
		return MODEL2002021.saveMod2002021(occam,mod200);
	}

	@Override
	public void deleteMod2002021(Occam occam, Mod2002021 mod200)
			throws AonCoreException {
		MODEL2002021.deleteMod2002021(occam, mod200);
	}

	@Override
	public Mod2002021 fillMod2002021AccountingData(Occam occam, Mod2002021 mod200, String base64) {
		byte[] fileData = Base64.getDecoder().decode(base64);
		ByteArrayInputStream input = new ByteArrayInputStream(fileData);
		try {
			JAXBContext context = JAXBContext.newInstance(MOD2002021.class);
			Unmarshaller um = context.createUnmarshaller();
			MOD2002021 mod = (MOD2002021) um.unmarshal(input);
			if (mod200 != null && mod != null) {
				XMLtoMod2002021.fillMod2002021(mod, mod200);
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
