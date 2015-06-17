package com.esferalia.aon.gwt.fiscal.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.occam.api.model.type.Activities.Type1Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type2Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type3Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type4Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type7Activities;
import com.esferalia.aon.occam.api.model.type.Activities.TypeActivity;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.jaxb.MOD2002014;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.jaxb.XMLtoMod2002014;
import com.esferalia.aon.occam.server.fiscal.format.mod200.Mod2002014Import2013;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "Fiscal Servlet", urlPatterns = { "/aon_gwt_fiscal/Fiscal" })
public class FiscalServiceImpl extends AonRemoteServiceServlet implements FiscalService {

	// ------------------------------------------------------- FISCAL PARAMETERS
	@Override
	public FiscalParameters getFiscalParameters(String domainName,int domain) throws AonCoreException {
		return AON.getFiscalParameters(domainName, domain);
	}

	@Override
	public FiscalModelMatrix getFiscalPanel(String domainName,
			int domain, int year) {
		return AON.getFiscalPanel(domainName, domain,year,this.getUserLogin());
	}
	
	@Override
	public LinkedList<IFiscalModel> getAllModels(String domainName,
			int domain) {
		return AON.getAllModels(domainName, domain,this.getUserLogin());
	}
	@Override
	public LinkedList<IFiscalModel> getAllModels(String domainName,
			int domain, int year) {
		return AON.getAllModels(domainName, domain,year,this.getUserLogin());
	}
	
	// ------------------------------------------------------ FISCAL ACTIVITIES
	@Override
	public FiscalActivity calculate(String domainName, FiscalActivity fa) {
		return AON.calculate(domainName, fa);
	}
	@Override
	public ArrayList<FiscalActivity> getFiscalActivities(String domainName,
			int domain) throws AonCoreException {
		return AON.getFiscalActivities(domainName, domain);
	}
	@Override
	public FiscalActivity getFiscalActivity(String domainName,
			int domain,int id) throws AonCoreException {
		return AON.getFiscalActivity(domainName, domain, id);
	}

	@Override
	public FiscalActivity getFiscalActivityFor(String domainName, Epigraph epigraph
			, FiscalActivity fa) {
		return AON.getFiscalActivityFor(domainName, epigraph, fa);
	}

	@Override
	public FiscalActivity save(String domainName, FiscalActivity fa) throws AonCoreException{
		return AON.save(domainName, fa);
		
	}

	@Override
	public void delete(String domainName, FiscalActivity fa) {
		AON.delete(domainName, fa);
	}

	// ------------------------------------------------------ FISCAL MODELS
	@Override
	public LinkedList<FiscalModel> getFiscalModels(String domainName,
			int domain) throws AonCoreException {
		return AON.getFiscalModels(domainName, domain);
	}
	@Override
	public FiscalModel getFiscalModel(String domainName,
			int domain,int id) throws AonCoreException {
		return AON.getFiscalModel(domainName, domain, id);
	}

	@Override
	public FiscalModel save(String domainName, FiscalModel fm) throws AonCoreException{
		return AON.save(domainName, fm);
		
	}

	@Override
	public void delete(String domainName, FiscalModel fm) {
		AON.delete(domainName, fm);
	}

	// -------------------------------------------------------------- ACTIVITIES
	@Override
	public ArrayList<Epigraph> getModuleEpigraphs(int year) {
		return new ArrayList<Epigraph>(Arrays.asList(Modules2015.Epigraph.values()));
	}
	
	@Override
	public ArrayList<Activity> getActivities(int activityGroup) throws AonCoreException {
		ArrayList<Activity> list = new ArrayList<Activity>();
		TypeActivity[] types = null;
		if (activityGroup == 0) {
			types = Type1Activities.values();
		} if (activityGroup == 1) {
			types = Type2Activities.values();
		} if (activityGroup == 2) {
			types = Type3Activities.values();
		} if (activityGroup == 3) {
			types = Type4Activities.values();
		} if (activityGroup == 6) {
			types = Type7Activities.values();
		}
		if (types == null) {
			throw new AonCoreException("Grupo de actividad no soportado " + activityGroup );
		}
		Activity a;
		for (TypeActivity type : types) {
			a = new Activity();
			a.setEpigraph(type.getEpigraph());
			a.setDescription(type.getLiteral());
			list.add(a);
		}
		return list;
	}
	
	// ---------------------------------------------------------------MODELO 190
	@Override
	public Mod190 initializeMod190(String domainName, Integer domain,Integer year) {
		return AON.initializeMod190(domainName, domain, year);
	}
	
	@Override
	public ArrayList<Mod190> getMod190s(String domainName, int domain) {
		return AON.getMod190s(domainName, domain);
	}

	@Override
	public void deleteMod190(String domainName, int domain, Mod190 mod190) {
		AON.deleteMod190(domainName, domain, mod190);
	}

	@Override
	public Mod190 saveMod190(String domainName, int domain,Mod190 mod190) {
		return AON.saveMod190(domainName, domain, mod190);
	}


	@Override
	public Mod190 getMod190(String domainName, int domain, Integer id) {
		return AON.getMod190(domainName, domain, id);
	}

	@Override
	public Mod190Detail getMod190Detail(String domainName, int domain, Integer id) {
		return AON.getMod190Detail(domainName, domain, id);
	}

	// ---------------------------------------------------------------MODELO 193
	@Override
	public Mod193 initializeMod193(String domainName, Integer domain,Integer year) {
		return AON.initializeMod193(domainName, domain, year);
	}
	
	@Override
	public ArrayList<Mod193> getMod193s(String domainName, int domain) {
		return AON.getMod193s(domainName, domain);
	}

	@Override
	public void deleteMod193(String domainName, int domain, Mod193 mod193) {
		AON.deleteMod193(domainName, domain, mod193);
	}

	@Override
	public Mod193 saveMod193(String domainName, int domain,Mod193 mod193) {
		return AON.saveMod193(domainName, domain, mod193);
	}


	@Override
	public Mod193 getMod193(String domainName, int domain, Integer id) {
		return AON.getMod193(domainName, domain, id);
	}

	@Override
	public Mod193Detail getMod193Detail(String domainName, int domain, Integer id) {
		return AON.getMod193Detail(domainName, domain, id);
	}

	// ---------------------------------------------------------------MODELO 180
	@Override
	public Mod180 initializeMod180(String domainName, Integer domain,Integer year) {
		return AON.initializeMod180(domainName, domain, year);
	}

	@Override
	public ArrayList<Mod180> getMod180s(String domainName, int domain) {
		return AON.getMod180s(domainName, domain);
	}

	@Override
	public void deleteMod180(String domainName, int domain, Mod180 mod180){
		AON.deleteMod180(domainName, domain, mod180);
	}

	@Override
	public Mod180 saveMod180(String domainName, int domain,Mod180 mod180) {
		return AON.saveMod180(domainName, domain, mod180);
	}


	@Override
	public Mod180 getMod180(String domainName, int domain, Integer id) {
		return AON.getMod180(domainName, domain, id);
	}

	@Override
	public Mod180Detail getMod180Detail(String domainName, int domain, Integer id) {
		return AON.getMod180Detail(domainName, domain, id);
	}

	// ---------------------------------------------------------------MODELO 184
	@Override
	public Mod184 initializeMod184(String domainName, Integer domain,Integer year) {
		return AON.initializeMod184(domainName, domain, year);
	}
	
	@Override
	public ArrayList<Mod184> getMod184s(String domainName, int domain) {
		return AON.getMod184s(domainName, domain);
	}

	@Override
	public void deleteMod184(String domainName, int domain, Mod184 mod184) {
		AON.deleteMod184(domainName, domain, mod184);
	}

	@Override
	public Mod184 saveMod184(String domainName, int domain,Mod184 mod184) {
		return AON.saveMod184(domainName, domain, mod184);
	}


	@Override
	public Mod184 getMod184(String domainName, int domain, Integer id) {
		return AON.getMod184(domainName, domain, id);
	}

	// ---------------------------------------------------------------MODELO 390
	@Override
	public Mod390 getMod390(String domainName, Integer domain,Integer id) {
		return AON.getMod390(domainName, domain, id);
	}

	@Override
	public ArrayList<Mod390> getMod390s(String domainName, Integer domain) {
		return AON.getMod390s(domainName, domain);
	}

	@Override
	public Mod390 saveMod390(String domainName, Integer domain, Mod390 mod390) {
		return AON.saveMod390(domainName, domain, mod390);
	}

	@Override
	public void deleteMod390(String domainName, Integer domain, Mod390 mod390) {
		AON.deleteMod390(domainName, domain, mod390);
	}
	
	@Override
	public ArrayList<Mod390Detail> getMod390Details(String domainName, Integer domain, Mod390 mod390) {
		return AON.getMod390Details(domainName, domain, mod390);
	}
	@Override
	public Mod390 initializeMod390(String domainName, Integer domain,Integer year) {
		return AON.initializeMod390(domainName, domain, year);
	}

	// ---------------------------------------------------------------MODELO 131
	@Override
	public Mod131 getMod131(String domainName,
			int domain,int id) throws AonCoreException {
		return AON.getMod131(domainName, domain, id);
	}

	@Override
	public LinkedList<Mod131> getMod131s(String domainName,
			int domain) throws AonCoreException {
		return AON.getMod131s(domainName, domain);
	}

	@Override
	public Mod131 calculateMod131(String domainName, Mod131 mod131) {
		return AON.calculate(domainName, mod131);
	}

	@Override
	public Mod131 saveMod131(String domainName, Mod131 mod131) {
		return AON.save(domainName, mod131);
	}

	@Override
	public void deleteMod131(String domainName, Mod131 mod131) {
		AON.deleteMod131(domainName, mod131);
	}

	@Override
	public Mod131 initializeMod131(String domainName, int domain, Mod131 mod131) {
		return AON.initializeMod131(domainName, domain, mod131);
	}

	// ---------------------------------------------------------------MODELO 202
	@Override
	public Mod202 getMod202(String domainName,
			int domain,int id) throws AonCoreException {
		return AON.getMod202(domainName, domain, id);
	}

	@Override
	public LinkedList<Mod202> getMod202s(String domainName,
			int domain) throws AonCoreException {
		return AON.getMod202s(domainName, domain);
	}

	@Override
	public Mod202 calculateMod202(String domainName, Mod202 mod202) {
		return AON.calculate(domainName, mod202);
	}

	@Override
	public Mod202 saveMod202(String domainName, Mod202 mod202) {
		return AON.save(domainName, mod202);
	}

	@Override
	public Mod202 initializeMod202(String domainName, int domain, Mod202 mod202) {
		return AON.initializeMod202(domainName, domain, mod202);
	}

	@Override
	public void deleteMod202(String domainName, Mod202 mod202) {
		AON.deleteMod202(domainName, mod202);
	}

	// ---------------------------------------------------------------MODELO 200 - 2013
	@Override
	public Mod2002013 initializeNewMod2002013(String domainName, int domain, Mod2002013 mod200) {
		return AON.initializeNewMod2002013(domainName,domain,mod200);
	}
	
	@Override
	public Mod2002013 initializeMod2002013(String domainName, int domain, Mod2002013 mod200) {
		return AON.initializeMod2002013(domainName,domain,mod200);
	}

	@Override
	public Mod2002013 getMod2002013ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return AON.getMod2002013ByYear(domainName,domain,year);
	}

	@Override
	public Mod2002013 getMod2002013ById(String domainName, int domain, int id)
			throws AonCoreException {
		return AON.getMod2002013ById(domainName,domain,id);
	}

	@Override
	public Mod2002013 calculateMod2002013(Mod2002013 mod200) throws AonCoreException {
		return AON.calculateMod2002013(mod200);
	}

	@Override
	public Mod2002013 saveMod2002013(String domainName, int domain, Mod2002013 mod200)
			throws AonCoreException {
		return AON.saveMod2002013(domainName,domain,mod200);
	}

	@Override
	public Mod2002013 validateMod2002013(Mod2002013 mod200) throws AonCoreException {
		return AON.validateMod2002013(mod200);
	}

	@Override
	public void deleteMod2002013(String domainName, int domain, int id)
			throws AonCoreException {
		AON.deleteMod2002013(domainName,domain,id);
	}

	@Override
	public String dumpAEATMod2002013(Mod2002013 mod200) throws AonCoreException {
		return AON.dumpAEATMod2002013(mod200);
	}
	
	// ---------------------------------------------------------------MODELO 200 - 2014
	@Override
	public Mod2002014 initializeNewMod2002014(String domainName, int domain, Mod2002014 mod200) {
		return AON.initializeNewMod2002014(domainName,domain,mod200);
	}
	
	@Override
	public Mod2002014 initializeMod2002014(String domainName, int domain, Mod2002014 mod200) {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			Mod2002013 mod2002013 = (Mod2002013) request.getSession().getAttribute("Mod2002013Import");
			if (mod2002013 != null) {
				if (!AonStringUtils.equals( mod2002013.getDocument(), mod200.getDocument())) {
					throw new AonCoreException("El NIF del documento importado no coincide");
				}
				Mod2002014Import2013.import2013(mod200, mod2002013);
				mod200.setInitializedFromLastYear(true);
			}
			return AON.initializeMod2002014(domainName,domain,mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002013Import");
		}
	}

	@Override
	public Mod2002014 getMod2002014ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return AON.getMod2002014ByYear(domainName,domain,year);
	}

	@Override
	public Mod2002014 getMod2002014ById(String domainName, int domain, int id)
			throws AonCoreException {
		return AON.getMod2002014ById(domainName,domain,id);
	}

	@Override
	public Mod2002014 calculateMod2002014(Mod2002014 mod200) throws AonCoreException {
		return AON.calculateMod2002014(mod200);
	}

	@Override
	public Mod2002014 saveMod2002014(String domainName, int domain, Mod2002014 mod200)
			throws AonCoreException {
		return AON.saveMod2002014(domainName,domain,mod200);
	}

	@Override
	public Mod2002014 validateMod2002014(Mod2002014 mod200) throws AonCoreException {
		return AON.validateMod2002014(mod200);
	}

	@Override
	public void deleteMod2002014(String domainName, int domain, int id)
			throws AonCoreException {
		AON.deleteMod2002014(domainName,domain,id);
	}

	@Override
	public String dumpAEATMod2002014(Mod2002014 mod200) throws AonCoreException {
		return AON.dumpAEATMod2002014(mod200);
	}

	@Override
	public Mod2002014 importMod2002013(String domainName, int domain,
			Mod2002014 mod200) throws AonCoreException {
		return AON.importMod2002013(domainName, domain, mod200);
	}
	
	@Override
	public Mod2002014 fillMod2002014AccountingData(Mod2002014 mod200)
			throws AonCoreException {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			MOD2002014 mod = (MOD2002014) request.getSession().getAttribute("Mod2002014Accounting");
			if (mod200 != null) {
				XMLtoMod2002014.fillMod2002014(mod, mod200);
			}
			return mod200; 
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002014Accounting");
		}
	}
	// --------------------------------------------------------------- NORMALIZED MEMORY
	@Override
	public Memory readMemory(Memory memory) throws AonCoreException {
		return null;
	}
	@Override
	public Memory saveMemory(Memory memory) throws AonCoreException {
		// TODO
		return null;
	}
	
	@Override
	public void deleteMemory(Memory memory) throws AonCoreException {
		// TODO
	}

	
}
