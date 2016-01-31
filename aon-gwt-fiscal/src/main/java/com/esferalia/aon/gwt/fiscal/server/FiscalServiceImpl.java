package com.esferalia.aon.gwt.fiscal.server;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.server.util.AONMVELUtils;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Activities.Type1Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type2Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type3Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type4Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type7Activities;
import com.esferalia.aon.occam.api.model.type.Activities.TypeActivity;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.jaxb.MOD2002014;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.jaxb.XMLtoMod2002014;
import com.esferalia.aon.occam.server.fiscal.format.mod200.Mod2002014Import2013;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Fiscal Servlet", urlPatterns = { "/aon_gwt_fiscal/Fiscal" })
public class FiscalServiceImpl extends AonRemoteServiceServlet implements FiscalService {

	@Override
	public Double mathExpression(String expression) throws AonCoreException {
		try {
			return AONMVELUtils.mathExpression(expression);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		}
	}
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
	public LinkedList<FiscalActivity> getFiscalActivities(String domainName,
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
	public LinkedList<Epigraph> getModuleEpigraphs(int year) {
		return new LinkedList<Epigraph>(Arrays.asList(Modules2015.Epigraph.values()));
	}
	
	@Override
	public LinkedList<Activity> getActivities(int activityGroup) throws AonCoreException {
		LinkedList<Activity> list = new LinkedList<Activity>();
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
		return AON.initializeMod190(domainName, domain, this.getUserLogin(), year);
	}
	
	@Override
	public LinkedList<Mod190> getMod190s(String domainName, int domain) {
		return AON.getMod190s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod190(String domainName, int domain, Mod190 mod190) {
		AON.deleteMod190(domainName, domain, this.getUserLogin(), mod190);
	}

	@Override
	public Mod190 saveMod190(String domainName, int domain,Mod190 mod190) {
		return AON.saveMod190(domainName, domain, this.getUserLogin(), mod190);
	}


	@Override
	public Mod190 getMod190(String domainName, int domain, Integer id) {
		return AON.getMod190(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod190Detail getMod190Detail(String domainName, int domain, Integer id) {
		return AON.getMod190Detail(domainName, domain, this.getUserLogin(), id);
	}

	// ---------------------------------------------------------------MODELO 193
	@Override
	public Mod193 initializeMod193(String domainName, Integer domain,Integer year) {
		return AON.initializeMod193(domainName, domain, this.getUserLogin(), year);
	}
	
	@Override
	public LinkedList<Mod193> getMod193s(String domainName, int domain) {
		return AON.getMod193s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod193(String domainName, int domain, Mod193 mod193) {
		AON.deleteMod193(domainName, domain, this.getUserLogin(), mod193);
	}

	@Override
	public Mod193 saveMod193(String domainName, int domain,Mod193 mod193) {
		return AON.saveMod193(domainName, domain, this.getUserLogin(), mod193);
	}


	@Override
	public Mod193 getMod193(String domainName, int domain, Integer id) {
		return AON.getMod193(domainName, domain, this.getUserLogin(), id);
	}

	// ---------------------------------------------------------------MODELO 180
	@Override
	public Mod180 initializeMod180(String domainName, Integer domain,Integer year) {
		return AON.initializeMod180(domainName, domain, this.getUserLogin(), year);
	}

	@Override
	public LinkedList<Mod180> getMod180s(String domainName, int domain) {
		return AON.getMod180s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod180(String domainName, int domain, Mod180 mod180){
		AON.deleteMod180(domainName, domain, this.getUserLogin(), mod180);
	}

	@Override
	public Mod180 saveMod180(String domainName, int domain,Mod180 mod180) {
		return AON.saveMod180(domainName, domain, this.getUserLogin(), mod180);
	}


	@Override
	public Mod180 getMod180(String domainName, int domain, Integer id) {
		return AON.getMod180(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod180Detail getMod180Detail(String domainName, int domain, Integer id) {
		return AON.getMod180Detail(domainName, domain, this.getUserLogin(), id);
	}

	// ---------------------------------------------------------------MODELO 184
	@Override
	public Mod184 initializeMod184(String domainName, Integer domain,Integer year) {
		return AON.initializeMod184(domainName, domain, this.getUserLogin(), year);
	}
	
	@Override
	public LinkedList<Mod184> getMod184s(String domainName, int domain) {
		return AON.getMod184s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod184(String domainName, int domain, Mod184 mod184) {
		AON.deleteMod184(domainName, domain, this.getUserLogin(), mod184);
	}

	@Override
	public Mod184 saveMod184(String domainName, int domain,Mod184 mod184) {
		return AON.saveMod184(domainName, domain, this.getUserLogin(), mod184);
	}


	@Override
	public Mod184 getMod184(String domainName, int domain, Integer id) {
		return AON.getMod184(domainName, domain, this.getUserLogin(), id);
	}

	// ---------------------------------------------------------------MODELO 390  
	@Override
	public LinkedList<Mod390> getMod390s(String domainName, Integer domain) {
		return AON.getMod390s(domainName, domain, this.getUserLogin());
	}

	// ---------------------------------------------------------------MODELO 390 - 2014 
	@Override
	public Mod3902014 getMod3902014(String domainName, Integer domain,Integer id) {
		return AON.getMod3902014(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod3902014 saveMod3902014(String domainName, Integer domain, Mod3902014 mod390) {
		return AON.saveMod3902014(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public void deleteMod3902014(String domainName, Integer domain, Mod3902014 mod390) {
		AON.deleteMod3902014(domainName, domain, this.getUserLogin(), mod390);
	}
	
	@Override
	public Mod3902014 initializeMod3902014(String domainName, Integer domain,Integer year) {
		return AON.initializeMod3902014(domainName, domain, this.getUserLogin(), year);
	}

	// ---------------------------------------------------------------MODELO 390 - 2015 
	@Override
	public Mod3902015 getMod3902015(String domainName, Integer domain,Integer id) {
		return AON.getMod3902015(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod3902015 saveMod3902015(String domainName, Integer domain, Mod3902015 mod390) {
		return AON.saveMod3902015(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public void deleteMod3902015(String domainName, Integer domain, Mod3902015 mod390) {
		AON.deleteMod3902015(domainName, domain, this.getUserLogin(), mod390);
	}
	
	@Override
	public Mod3902015 initializeMod3902015(String domainName, Integer domain,Integer year) {
		return AON.initializeMod3902015(domainName, domain, this.getUserLogin(), year);
	}

	// ---------------------------------------------------------------MODELO 111
	@Override
	public Mod111 getMod111(String domainName,
			int domain,int id) throws AonCoreException {
		return AON.getMod111(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod111> getMod111s(String domainName,
			int domain) throws AonCoreException {
		return AON.getMod111s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod111 calculateMod111(String domainName, Mod111 mod111) {
		return AON.calculate(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 saveMod111(String domainName, Mod111 mod111) {
		return AON.save(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 initializeMod111(String domainName, int domain, Mod111 mod111) {
		return AON.initializeMod111(domainName, domain, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 createMod111(String domainName, int domain, Mod111 mod111) {
		return AON.createMod111(domainName, domain, this.getUserLogin(), mod111);
	}

	@Override
	public void deleteMod111(String domainName, Mod111 mod111) {
		AON.deleteMod111(domainName, this.getUserLogin(), mod111);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod111 mod111, Mod111Key key, Mod111KeyInfo infoKey)
			throws AonCoreException {
		return AON.getMod111Info(domainName, domain, this.getUserLogin(), mod111, key, infoKey);
		
	}

	// ---------------------------------------------------------------MODELO 131
	@Override
	public Mod131 getMod131(String domainName,
			int domain,int id) throws AonCoreException {
		return AON.getMod131(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod131> getMod131s(String domainName,
			int domain) throws AonCoreException {
		return AON.getMod131s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod131 calculateMod131(String domainName, Mod131 mod131) {
		return AON.calculate(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 saveMod131(String domainName, Mod131 mod131) {
		return AON.save(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public void deleteMod131(String domainName, Mod131 mod131) {
		AON.deleteMod131(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 initializeMod131(String domainName, int domain, Mod131 mod131) {
		return AON.initializeMod131(domainName, domain, this.getUserLogin(), mod131);
	}

	// ---------------------------------------------------------------MODELO 202
	@Override
	public Mod202 getMod202(String domainName,
			int domain,int id) throws AonCoreException {
		return AON.getMod202(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod202> getMod202s(String domainName,
			int domain) throws AonCoreException {
		return AON.getMod202s(domainName, domain,this.getUserLogin());
	}

	@Override
	public Mod202 calculateMod202(String domainName, Mod202 mod202) {
		return AON.calculate(domainName,this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 saveMod202(String domainName, Mod202 mod202) {
		return AON.save(domainName,this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 initializeMod202(String domainName, int domain, Mod202 mod202) {
		return AON.initializeMod202(domainName, domain,this.getUserLogin(), mod202);
	}

	@Override
	public void deleteMod202(String domainName, Mod202 mod202) {
		AON.deleteMod202(domainName,this.getUserLogin(), mod202);
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
	public Mod2002014 createMod2002014(String domainName, int domain, int year)
			throws AonCoreException {
		return AON.createMod2002014(domainName,domain,year);
	}

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
	
	// --------------------------------------------------------------- ACCOUNT PERIOD
	@Override
	public LinkedList<AccountPeriod> getDomainPeriods(String domainName,
			int domain) throws AonCoreException {
		return AON.getDomainPeriods(domainName, domain, this.getUserLogin());
	}
	// --------------------------------------------------------------- ACCOUNT ENTRIES
	@Override
	public LinkedList<AccountEntry> getAccountEntries(String domainName,
			int domain, final AccountEntryParams params,int offset, int limit) throws AonCoreException {
		return AON.getAccountEntries(domainName, domain, this.getUserLogin(),
				params, offset, limit);
	}

	@Override
	public AccountEntry getAccountEntry(String domainName, int domain, int id)
			throws AonCoreException {
		LinkedList<AccountEntry> list = AON.getAccountEntries(
				domainName, domain, this.getUserLogin(), 
				p -> p.getIdProperty().eq(id)
				, 0, 1)
				;
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.getFirst();
	}

	@Override
	public AccountEntry save(String domainName, int domain, AccountEntry ae)
			throws AonCoreException {
		return AON.save(domainName, domain, this.getUserLogin(), ae);
	}
	@Override
	public LinkedList<AccountEntry> getSalaryAccountEntries(String domainName,
			int domain, Date from, Date to ) {
		return AON.getAccountEntries(domainName, domain, this.getUserLogin(), 
				p -> p.getDomainProperty().eq(domain)
					.and(p.getEntryDateProperty().between(from, to))
					.and(p.getEntryTypeProperty().eq((byte) AccountEntryType.SALARY.ordinal()))
				, 0, 1000);
	}

	@Override
	public void deleteAccountEntry(String domainName, int domain, Integer id) {
		AON.deleteAccountEntry(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<AccountEntry> insertSalaryAccountEntries(
			String domainName, int domain, Date from, Date to, String concept,
			Integer registryBank) {
		List<Integer> ids = AON.insertSalaryEntries(domainName, domain,
				this.getUserLogin() , from, to, concept, registryBank);
		final Integer[] arr = ids.toArray(new Integer[ids.size()]);  
		return AON.getAccountEntries(domainName, domain, this.getUserLogin()
				, p -> p.getIdProperty().in(arr)
						.and(p.getDomainProperty().eq(domain) )
				, 0, 100);
	}
	@Override
	public AccountStatementReport getAccountStatement(String domainName,
			int domain, AccountStatementParams params) throws AonCoreException {
		return AON.getAccountStatement(domainName,domain,this.getUserLogin(),params);
	}
	@Override
	public LinkedList<AccountStatement> getAccountBalance(String domainName,
			int domain, AccountStatementParams params) throws AonCoreException {
		return AON.getAccountBalance(domainName,domain,this.getUserLogin(),params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
}
