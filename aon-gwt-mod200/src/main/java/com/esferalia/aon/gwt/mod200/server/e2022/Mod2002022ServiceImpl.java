package com.esferalia.aon.gwt.mod200.server.e2022;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Mod2002022Service;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.MODEL2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022.jaxb.MOD2002022;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022.jaxb.XMLtoMod2002022;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 2022 Servlet", urlPatterns = { "/aon_gwt_mod200/ms/Mod2002022" })
public class Mod2002022ServiceImpl extends AonStatelessRemoteServiceServlet implements Mod2002022Service {
	
	@Override
	public Mod2002022 createMod2002022(Occam occam, int year)
			throws AonCoreException {
		return MODEL2002022.createMod2002022(occam,year);
	}

	@Override
	public Mod2002022 initializeMod2002022(Occam occam, Mod2002022 mod200) {
		return MODEL2002022.initializeMod2002022(occam,mod200);
	}

	@Override
	public Mod2002022 getMod2002022ById(Occam occam, int id)
			throws AonCoreException {
		return MODEL2002022.getMod2002022ById(occam,id);
	}

	@Override
	public Mod2002022 calculateMod2002022(Mod2002022 mod200) throws AonCoreException {
		return MODEL2002022.calculateMod2002022(mod200);
	}

	@Override
	public Mod2002022 saveMod2002022(Occam occam, Mod2002022 mod200)
			throws AonCoreException {
		return MODEL2002022.saveMod2002022(occam,mod200);
	}

	@Override
	public void deleteMod2002022(Occam occam, Mod2002022 mod200)
			throws AonCoreException {
		MODEL2002022.deleteMod2002022(occam, mod200);
	}

	// FALTA
//	@Override
//	public Mod2002022 fillMod2002022AccountingData(Occam occam, Mod2002022 mod200, String base64) {
//		byte[] fileData = Base64.getDecoder().decode(base64);
//		ByteArrayInputStream input = new ByteArrayInputStream(fileData);
//		try {
//			JAXBContext context = JAXBContext.newInstance(MOD2002022.class);
//			Unmarshaller um = context.createUnmarshaller();
//			MOD2002022 mod = (MOD2002022) um.unmarshal(input);
//			if (mod200 != null && mod != null) {
//				XMLtoMod2002022.fillMod2002022(mod, mod200);
//			}
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		}		
//		return mod200;
//	}
	@Override
	public Mod2002022 fillMod2002022AccountingData(Occam occam, Mod2002022 mod200, String base64) {
		byte[] fileData = Base64.getDecoder().decode(base64);
		ByteArrayInputStream input = new ByteArrayInputStream(fileData);
		try {
			JAXBContext context = JAXBContext.newInstance(MOD2002022.class);
			Unmarshaller um = context.createUnmarshaller();
			MOD2002022 mod = (MOD2002022) um.unmarshal(input);
			if (mod200 != null && mod != null) {
				XMLtoMod2002022.fillMod2002022(mod, mod200);
			}
		} catch (JAXBException e) {
			//e.printStackTrace();
			// FALTA - MENSAJE DESCRIPTIVO DEL ERROR
			throw new AonCoreException("Error al cargar el archivo. El archivo debe ser un archivo XML con el formato indicado por la Agencia Tributaria " + e.getMessage());
		}		
		return mod200;
	}
	
	
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(Occam occam) throws AonCoreException {
		return AON.getCompanyBanks(occam.getDomainName(),occam.getDomain(), occam.getUser());
	}
}
