package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
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
import com.esferalia.aon.occam.api.model.fiscal.mod111.Model111ScriptProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod115.Model115ScriptProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod123.Model123ScriptProvider;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Activities.Type1Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type2Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type3Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type4Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type7Activities;
import com.esferalia.aon.occam.api.model.type.Activities.TypeActivity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.jaxb.MOD2002014;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.jaxb.XMLtoMod2002014;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2015.jaxb.MOD2002015;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2015.jaxb.XMLtoMod2002015;
import com.esferalia.aon.occam.server.fiscal.format.mod200.Mod2002014Import2013;
import com.esferalia.aon.occam.server.fiscal.format.mod200.Mod2002015Import2014;
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
		return AON.getFiscalParameters(domainName, domain,this.getUserLogin());
	}

	@Override
	public FiscalModelMatrix getFiscalPanel(String domainName,
			int domain, int year) {
		return FISCAL.getFiscalPanel(domainName, domain,year,this.getUserLogin());
	}
	
	@Override
	public LinkedList<IFiscalModel> getAllModels(String domainName,
			int domain) {
		return FISCAL.getAllModels(domainName, domain,this.getUserLogin());
	}
	@Override
	public LinkedList<IFiscalModel> getAllModels(String domainName,
			int domain, int year) {
		return FISCAL.getAllModels(domainName, domain,year,this.getUserLogin());
	}
	
	// -------------------------------------------------------------- ACTIVITIES
	
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
		return FISCAL.initializeMod190(domainName, domain, this.getUserLogin(), year);
	}
	
	@Override
	public LinkedList<Mod190> getMod190s(String domainName, int domain) {
		return FISCAL.getMod190s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod190(String domainName, int domain, Mod190 mod190) {
		FISCAL.deleteMod190(domainName, domain, this.getUserLogin(), mod190);
	}

	@Override
	public Mod190 saveMod190(String domainName, int domain,Mod190 mod190) {
		return FISCAL.saveMod190(domainName, domain, this.getUserLogin(), mod190);
	}


	@Override
	public Mod190 getMod190(String domainName, int domain, Integer id) {
		return FISCAL.getMod190(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod190Detail getMod190Detail(String domainName, int domain, Integer id) {
		return FISCAL.getMod190Detail(domainName, domain, this.getUserLogin(), id);
	}

	// ---------------------------------------------------------------MODELO 193
	@Override
	public Mod193 initializeMod193(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod193(domainName, domain, this.getUserLogin(), year);
	}
	
	@Override
	public LinkedList<Mod193> getMod193s(String domainName, int domain) {
		return FISCAL.getMod193s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod193(String domainName, int domain, Mod193 mod193) {
		FISCAL.deleteMod193(domainName, domain, this.getUserLogin(), mod193);
	}

	@Override
	public Mod193 saveMod193(String domainName, int domain,Mod193 mod193) {
		return FISCAL.saveMod193(domainName, domain, this.getUserLogin(), mod193);
	}


	@Override
	public Mod193 getMod193(String domainName, int domain, Integer id) {
		return FISCAL.getMod193(domainName, domain, this.getUserLogin(), id);
	}

	// ---------------------------------------------------------------MODELO 180
	@Override
	public Mod180 initializeMod180(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod180(domainName, domain, this.getUserLogin(), year);
	}

	@Override
	public LinkedList<Mod180> getMod180s(String domainName, int domain) {
		return FISCAL.getMod180s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod180(String domainName, int domain, Mod180 mod180){
		FISCAL.deleteMod180(domainName, domain, this.getUserLogin(), mod180);
	}

	@Override
	public Mod180 saveMod180(String domainName, int domain,Mod180 mod180) {
		return FISCAL.saveMod180(domainName, domain, this.getUserLogin(), mod180);
	}


	@Override
	public Mod180 getMod180(String domainName, int domain, Integer id) {
		return FISCAL.getMod180(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod180Detail getMod180Detail(String domainName, int domain, Integer id) {
		return FISCAL.getMod180Detail(domainName, domain, this.getUserLogin(), id);
	}

	// ---------------------------------------------------------------MODELO 184
	@Override
	public Mod184 initializeMod184(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod184(domainName, domain, this.getUserLogin(), year);
	}
	
	@Override
	public LinkedList<Mod184> getMod184s(String domainName, int domain) {
		return FISCAL.getMod184s(domainName, domain, this.getUserLogin());
	}

	@Override
	public void deleteMod184(String domainName, int domain, Mod184 mod184) {
		FISCAL.deleteMod184(domainName, domain, this.getUserLogin(), mod184);
	}

	@Override
	public Mod184 saveMod184(String domainName, int domain,Mod184 mod184) {
		return FISCAL.saveMod184(domainName, domain, this.getUserLogin(), mod184);
	}


	@Override
	public Mod184 getMod184(String domainName, int domain, Integer id) {
		return FISCAL.getMod184(domainName, domain, this.getUserLogin(), id);
	}

	// ---------------------------------------------------------------MODELO 390  
	@Override
	public LinkedList<Mod390> getMod390s(String domainName, Integer domain) {
		return FISCAL.getMod390s(domainName, domain, this.getUserLogin());
	}

	// ---------------------------------------------------------------MODELO 390 - 2014 
	@Override
	public Mod3902014 getMod3902014(String domainName, Integer domain,Integer id) {
		return FISCAL.getMod3902014(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod3902014 saveMod3902014(String domainName, Integer domain, Mod3902014 mod390) {
		return FISCAL.saveMod3902014(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public void deleteMod3902014(String domainName, Integer domain, Mod3902014 mod390) {
		FISCAL.deleteMod3902014(domainName, domain, this.getUserLogin(), mod390);
	}
	
	@Override
	public Mod3902014 initializeMod3902014(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod3902014(domainName, domain, this.getUserLogin(), year);
	}

	// ---------------------------------------------------------------MODELO 390 - 2015 
	@Override
	public Mod3902015 getMod3902015(String domainName, Integer domain,Integer id) {
		return FISCAL.getMod3902015(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public Mod3902015 saveMod3902015(String domainName, Integer domain, Mod3902015 mod390) {
		return FISCAL.saveMod3902015(domainName, domain, this.getUserLogin(), mod390);
	}

	@Override
	public void deleteMod3902015(String domainName, Integer domain, Mod3902015 mod390) {
		FISCAL.deleteMod3902015(domainName, domain, this.getUserLogin(), mod390);
	}
	
	@Override
	public Mod3902015 initializeMod3902015(String domainName, Integer domain,Integer year) {
		return FISCAL.initializeMod3902015(domainName, domain, this.getUserLogin(), year);
	}

	// ---------------------------------------------------------------MODELO 111
	@Override
	public Mod111 getMod111(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod111(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod111> getMod111s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod111s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod111 calculateMod111(String domainName, Mod111 mod111) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 saveMod111(String domainName, Mod111 mod111) {
		return FISCAL.save(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 saveCommentsMod111(String domainName, Mod111 mod111) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 initializeForFinishMod111(String domainName, Mod111 mod111) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 finishMod111(String domainName, Mod111 mod111) {
		return FISCAL.finish(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 reopenMod111(String domainName, Mod111 mod111) {
		return FISCAL.reopen(domainName, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 initializeMod111(String domainName, int domain, Mod111 mod111) {
		return FISCAL.initializeMod111(domainName, domain, this.getUserLogin(), mod111);
	}

	@Override
	public Mod111 createMod111(String domainName, int domain, Mod111 mod111) {
		return FISCAL.createMod111(domainName, domain, this.getUserLogin(), mod111);
	}

	@Override
	public void deleteMod111(String domainName, Mod111 mod111) {
		FISCAL.deleteMod111(domainName, this.getUserLogin(), mod111);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod111Info(domainName, domain, this.getUserLogin(), mod111, script, infoKey);
		
	}

	// ---------------------------------------------------------------MODELO 115
	@Override
	public Mod115 getMod115(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod115(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod115> getMod115s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod115s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod115 calculateMod115(String domainName, Mod115 mod115) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 saveMod115(String domainName, Mod115 mod115) {
		return FISCAL.save(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 saveCommentsMod115(String domainName, Mod115 mod115) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 initializeForFinishMod115(String domainName, Mod115 mod115) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 finishMod115(String domainName, Mod115 mod115) {
		return FISCAL.finish(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 reopenMod115(String domainName, Mod115 mod115) {
		return FISCAL.reopen(domainName, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 initializeMod115(String domainName, int domain, Mod115 mod115) {
		return FISCAL.initializeMod115(domainName, domain, this.getUserLogin(), mod115);
	}

	@Override
	public Mod115 createMod115(String domainName, int domain, Mod115 mod115) {
		return FISCAL.createMod115(domainName, domain, this.getUserLogin(), mod115);
	}

	@Override
	public void deleteMod115(String domainName, Mod115 mod115) {
		FISCAL.deleteMod115(domainName, this.getUserLogin(), mod115);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod115Info(domainName, domain, this.getUserLogin(), mod115, script, infoKey);
		
	}

	// ---------------------------------------------------------------MODELO 123
	@Override
	public Mod123 getMod123(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod123(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod123> getMod123s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod123s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod123 calculateMod123(String domainName, Mod123 mod123) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 saveMod123(String domainName, Mod123 mod123) {
		return FISCAL.save(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 saveCommentsMod123(String domainName, Mod123 mod123) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 initializeForFinishMod123(String domainName, Mod123 mod123) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 finishMod123(String domainName, Mod123 mod123) {
		return FISCAL.finish(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 reopenMod123(String domainName, Mod123 mod123) {
		return FISCAL.reopen(domainName, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 initializeMod123(String domainName, int domain, Mod123 mod123) {
		return FISCAL.initializeMod123(domainName, domain, this.getUserLogin(), mod123);
	}

	@Override
	public Mod123 createMod123(String domainName, int domain, Mod123 mod123) {
		return FISCAL.createMod123(domainName, domain, this.getUserLogin(), mod123);
	}

	@Override
	public void deleteMod123(String domainName, Mod123 mod123) {
		FISCAL.deleteMod123(domainName, this.getUserLogin(), mod123);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod123Info(domainName, domain, this.getUserLogin(), mod123, script, infoKey);
		
	}

	// ---------------------------------------------------------------MODELO 130
	@Override
	public Mod130 getMod130(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod130(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod130> getMod130s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod130s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod130 calculateMod130(String domainName, Mod130 mod130) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 saveMod130(String domainName, Mod130 mod130) {
		return FISCAL.save(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 saveCommentsMod130(String domainName, Mod130 mod130) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 initializeForFinishMod130(String domainName, Mod130 mod130) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 finishMod130(String domainName, Mod130 mod130) {
		return FISCAL.finish(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 reopenMod130(String domainName, Mod130 mod130) {
		return FISCAL.reopen(domainName, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 initializeMod130(String domainName, int domain, Mod130 mod130) {
		return FISCAL.initializeMod130(domainName, domain, this.getUserLogin(), mod130);
	}

	@Override
	public Mod130 createMod130(String domainName, int domain, Mod130 mod130) {
		return FISCAL.createMod130(domainName, domain, this.getUserLogin(), mod130);
	}

	@Override
	public void deleteMod130(String domainName, Mod130 mod130) {
		FISCAL.deleteMod130(domainName, this.getUserLogin(), mod130);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod130Info(domainName, domain, this.getUserLogin(), mod130, script, infoKey);
		
	}

	// ---------------------------------------------------------------MODELO 131
	@Override
	public Mod131 getMod131(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod131(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod131> getMod131s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod131s(domainName, domain, this.getUserLogin());
	}

	@Override
	public Mod131 calculateMod131(String domainName, Mod131 mod131) {
		return FISCAL.calculate(domainName, this.getUserLogin(), mod131);
	}
	@Override
	public Mod131Activity calculateMod131Activity(String domainName,int domain, Mod131Activity activity) throws AonCoreException {
		return FISCAL.calculate(domainName, domain, this.getUserLogin(), activity);
	}

	@Override
	public Mod131 saveMod131(String domainName, Mod131 mod131) {
		return FISCAL.save(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 saveCommentsMod131(String domainName, Mod131 mod131) {
		return FISCAL.saveComments(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 initializeForFinishMod131(String domainName, Mod131 mod131) {
		return FISCAL.initializeForFinish(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 finishMod131(String domainName, Mod131 mod131) {
		return FISCAL.finish(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 reopenMod131(String domainName, Mod131 mod131) {
		return FISCAL.reopen(domainName, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 initializeMod131(String domainName, int domain, Mod131 mod131) {
		return FISCAL.initializeMod131(domainName, domain, this.getUserLogin(), mod131);
	}

	@Override
	public Mod131 createMod131(String domainName, int domain, Mod131 mod131) {
		return FISCAL.createMod131(domainName, domain, this.getUserLogin(), mod131);
	}

	@Override
	public void deleteMod131(String domainName, Mod131 mod131) {
		FISCAL.deleteMod131(domainName, this.getUserLogin(), mod131);
	}
	@Override
	public String getInfo(String domainName, int domain, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey)
			throws AonCoreException {
		return FISCAL.getMod131Info(domainName, domain, this.getUserLogin(), mod131, script, infoKey);
		
	}
	
	// ---------------------------------------------------------------MODELO 202
	@Override
	public Mod202 getMod202(String domainName,
			int domain,int id) throws AonCoreException {
		return FISCAL.getMod202(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public LinkedList<Mod202> getMod202s(String domainName,
			int domain) throws AonCoreException {
		return FISCAL.getMod202s(domainName, domain,this.getUserLogin());
	}

	@Override
	public Mod202 calculateMod202(String domainName, Mod202 mod202) {
		return FISCAL.calculate(domainName,this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 saveMod202(String domainName, Mod202 mod202) {
		return FISCAL.save(domainName,this.getUserLogin(), mod202);
	}

	@Override
	public Mod202 initializeMod202(String domainName, int domain, Mod202 mod202) {
		return FISCAL.initializeMod202(domainName, domain,this.getUserLogin(), mod202);
	}

	@Override
	public void deleteMod202(String domainName, Mod202 mod202) {
		FISCAL.deleteMod202(domainName,this.getUserLogin(), mod202);
	}

	// ---------------------------------------------------------------MODELO 200 - 2013
	@Override
	public Mod2002013 initializeNewMod2002013(String domainName, int domain, Mod2002013 mod200) {
		return FISCAL.initializeNewMod2002013(domainName,domain,this.getUserLogin(),mod200);
	}
	
	@Override
	public Mod2002013 initializeMod2002013(String domainName, int domain, Mod2002013 mod200) {
		return FISCAL.initializeMod2002013(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002013 getMod2002013ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002013ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002013 getMod2002013ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002013ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002013 calculateMod2002013(Mod2002013 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002013(mod200);
	}

	@Override
	public Mod2002013 saveMod2002013(String domainName, int domain, Mod2002013 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002013(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002013 validateMod2002013(Mod2002013 mod200) throws AonCoreException {
		return FISCAL.validateMod2002013(mod200);
	}

	@Override
	public void deleteMod2002013(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002013(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002013(Mod2002013 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002013(mod200);
	}
	
	// ---------------------------------------------------------------MODELO 200 - 2014

	@Override
	public Mod2002014 createMod2002014(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.createMod2002014(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002014 initializeNewMod2002014(String domainName, int domain, Mod2002014 mod200) {
		return FISCAL.initializeNewMod2002014(domainName,domain,this.getUserLogin(),mod200);
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
			return FISCAL.initializeMod2002014(domainName,domain,this.getUserLogin(),mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002013Import");
		}
	}

	@Override
	public Mod2002014 getMod2002014ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002014ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002014 getMod2002014ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002014ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002014 calculateMod2002014(Mod2002014 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002014(mod200);
	}

	@Override
	public Mod2002014 saveMod2002014(String domainName, int domain, Mod2002014 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002014(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002014 validateMod2002014(Mod2002014 mod200) throws AonCoreException {
		return FISCAL.validateMod2002014(mod200);
	}

	@Override
	public void deleteMod2002014(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002014(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002014(Mod2002014 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002014(mod200);
	}

	@Override
	public Mod2002014 importMod2002013(String domainName, int domain,
			Mod2002014 mod200) throws AonCoreException {
		return FISCAL.importMod2002013(domainName, domain,this.getUserLogin(), mod200);
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

	// ---------------------------------------------------------------MODELO 200 - 2015
	@Override
	public Mod2002015 createMod2002015(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.createMod2002015(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002015 initializeNewMod2002015(String domainName, int domain, Mod2002015 mod200) {
		return FISCAL.initializeNewMod2002015(domainName,domain,this.getUserLogin(),mod200);
	}
	
	@Override
	public Mod2002015 initializeMod2002015(String domainName, int domain, Mod2002015 mod200) {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			Mod2002014 mod2002014 = (Mod2002014) request.getSession().getAttribute("Mod2002014Import");
			if (mod2002014 != null) {
				if (!AonStringUtils.equals( mod2002014.getDocument(), mod200.getDocument())) {
					throw new AonCoreException("El NIF del documento importado no coincide");
				}
				Mod2002015Import2014.import2014(mod200, mod2002014);
				mod200.setInitializedFromLastYear(true);
			}
			return FISCAL.initializeMod2002015(domainName,domain,this.getUserLogin(),mod200);
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002013Import");
		}
	}

	@Override
	public Mod2002015 getMod2002015ByYear(String domainName, int domain, int year)
			throws AonCoreException {
		return FISCAL.getMod2002015ByYear(domainName,domain,this.getUserLogin(),year);
	}

	@Override
	public Mod2002015 getMod2002015ById(String domainName, int domain, int id)
			throws AonCoreException {
		return FISCAL.getMod2002015ById(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public Mod2002015 calculateMod2002015(Mod2002015 mod200) throws AonCoreException {
		return FISCAL.calculateMod2002015(mod200);
	}

	@Override
	public Mod2002015 saveMod2002015(String domainName, int domain, Mod2002015 mod200)
			throws AonCoreException {
		return FISCAL.saveMod2002015(domainName,domain,this.getUserLogin(),mod200);
	}

	@Override
	public Mod2002015 validateMod2002015(Mod2002015 mod200) throws AonCoreException {
		return FISCAL.validateMod2002015(mod200);
	}

	@Override
	public void deleteMod2002015(String domainName, int domain, int id)
			throws AonCoreException {
		FISCAL.deleteMod2002015(domainName,domain,this.getUserLogin(),id);
	}

	@Override
	public String dumpAEATMod2002015(Mod2002015 mod200) throws AonCoreException {
		return FISCAL.dumpAEATMod2002015(mod200);
	}

	@Override
	public Mod2002015 importMod2002014(String domainName, int domain,
			Mod2002015 mod200) throws AonCoreException {
		return FISCAL.importMod2002014(domainName, domain,this.getUserLogin(), mod200);
	}
	
	@Override
	public Mod2002015 fillMod2002015AccountingData(Mod2002015 mod200)
			throws AonCoreException {
		HttpServletRequest request = getThreadLocalRequest();
		try {
			MOD2002015 mod = (MOD2002015) request.getSession().getAttribute("Mod2002015Accounting");
			if (mod200 != null) {
				XMLtoMod2002015.fillMod2002015(mod, mod200);
			}
			return mod200; 
		} catch ( Throwable t) {
			throw new AonCoreException(t);
		} finally {
			request.getSession().removeAttribute("Mod2002015Accounting");
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
		return ACCOUNTING.getDomainPeriods(domainName, domain, this.getUserLogin());
	}
	// --------------------------------------------------------------- ACCOUNT ENTRIES
	@Override
	public LinkedList<AccountEntry> getAccountEntries(String domainName,
			int domain, final AccountEntryParams params,int offset, int limit) throws AonCoreException {
		return ACCOUNTING.getAccountEntries(domainName, domain, this.getUserLogin(),
				params, offset, limit);
	}

	@Override
	public AccountEntry getAccountEntry(String domainName, int domain, int id)
			throws AonCoreException {
		LinkedList<AccountEntry> list = ACCOUNTING.getAccountEntries(
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
		return ACCOUNTING.save(domainName, domain, this.getUserLogin(), ae);
	}
	@Override
	public LinkedList<AccountEntry> getSalaryAccountEntries(String domainName,
			int domain, Date from, Date to ) {
		return ACCOUNTING.getAccountEntries(domainName, domain, this.getUserLogin(), 
				p -> p.getDomainProperty().eq(domain)
					.and(p.getEntryDateProperty().between(from, to))
					.and(p.getEntryTypeProperty().eq((byte) AccountEntryType.SALARY.ordinal()))
				, 0, 1000);
	}

	@Override
	public LinkedList<SalaryEntry> getSalaryEntries(String domainName,
			int domain, Date from, Date to ) {
		return ACCOUNTING.getSalaryEntries(domainName, domain, this.getUserLogin(),from,to);
	}

	@Override
	public void deleteAccountEntry(String domainName, int domain, Integer id) {
		ACCOUNTING.deleteAccountEntry(domainName, domain, this.getUserLogin(), id);
	}

	@Override
	public AccountingInvoice initializeInvoice(String domainName, int domain, 
			AccountingRegistry registry, Date issueDate)
			throws AonCoreException {
		return ACCOUNTING.initializeInvoice(domainName, domain, this.getUserLogin(), 
				registry, issueDate);
	}

	@Override
	public AccountingInvoice getAccountingInvoice(String domainName, int domain, Integer accountEntry)
			throws AonCoreException {
		return ACCOUNTING.getAccountingInvoice(domainName, domain, this.getUserLogin(), accountEntry);
	}

	@Override
	public AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, Integer invoiceId)
			throws AonCoreException {
		return ACCOUNTING.getAccountingInvoiceFromInvoice(domainName, domain, this.getUserLogin(), invoiceId);
	}

	@Override
	public AccountingInvoice getRegistryLastAccountingInvoice(String domainName, int domain,
			Integer registryId) {
		return ACCOUNTING.getRegistryLastAccountingInvoice(domainName, domain, this.getUserLogin(), registryId);
	}
	
	@Override
	public AccountingInvoice rectifyInvoice(String domainName, int domain, Integer invoiceId,
			InvoiceRectificationData data) throws AonCoreException {
		return ACCOUNTING.rectifyInvoice(domainName, domain, this.getUserLogin(), invoiceId, data );
	}

	@Override
	public AccountingInvoice save(String domainName, int domain, AccountingInvoice invoice)
			throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, this.getUserLogin(), invoice);
	}

	@Override
	public LinkedList<AccountEntry> insertSalaryAccountEntries(
			String domainName, int domain, Date from, Date to, String concept,
			Integer registryBank) {
		List<Integer> ids = ACCOUNTING.insertSalaryEntries(domainName, domain,
				this.getUserLogin() , from, to, concept, registryBank);
		final Integer[] arr = ids.toArray(new Integer[ids.size()]);  
		return ACCOUNTING.getAccountEntries(domainName, domain, this.getUserLogin()
				, p -> p.getIdProperty().in(arr)
						.and(p.getDomainProperty().eq(domain) )
				, 0, 100);
	}

	@Override
	public LinkedList<AccountEntry> previewSalaryAccountEntries(
			String domainName, int domain, Date from, Date to, String concept,
			Integer registryBank) {
		return ACCOUNTING.previewSalaryEntries(domainName, domain,
				this.getUserLogin() , from, to, concept, registryBank);
	}

	@Override
	public AccountStatementReport getAccountStatement(String domainName,
			int domain, AccountStatementParams params) throws AonCoreException {
		return ACCOUNTING.getAccountStatement(domainName,domain,this.getUserLogin(),params);
	}
	@Override
	public LinkedList<AccountStatement> getAccountBalance(String domainName,
			int domain, AccountStatementParams params) throws AonCoreException {
		return ACCOUNTING.getAccountBalance(domainName,domain,this.getUserLogin(),params)
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public Attach getMod111Attach(String domainName, Mod111 mod111){
		Mod111ExcelAction action = new Mod111ExcelAction(mod111);
		action.initialize(mod111.getModel().getName(mod111.getAdministration(), mod111.getPeriod()));
		ByteArrayOutputStream output = new ByteArrayOutputStream();			
		for (IModelScript<Mod111Key> ms : Model111ScriptProvider.obtainScript(mod111)) {
			action.accept(ms);
		}
		action.beforeFinalize();
		try {
			action.finalize(output);
		} catch (IOException e) {e.printStackTrace();}
		
		return new Attach(new Domain().setId(mod111.getDomain()).setName(domainName)
				, mod111.getId(), AttachType.MOD111, "Modelo 111", MimeType.MS_EXCEL, null, output.toByteArray());
	}
	
	public Attach getMod115Attach(String domainName, Mod115 mod115){
		Mod115ExcelAction action = new Mod115ExcelAction(mod115);
		action.initialize(mod115.getModel().getName(mod115.getAdministration(), mod115.getPeriod()));
		ByteArrayOutputStream output = new ByteArrayOutputStream();			
		for (IModelScript<Mod115Key> ms : Model115ScriptProvider.obtainScript(mod115)) {
			action.accept(ms);
		}
		action.beforeFinalize();
		try {
			action.finalize(output);
		} catch (IOException e) {e.printStackTrace();}
		
		return new Attach(new Domain().setId(mod115.getDomain()).setName(domainName)
				, mod115.getId(), AttachType.MOD115, "Modelo 115", MimeType.MS_EXCEL, null, output.toByteArray());
	}
	
	public Attach getMod123Attach(String domainName, Mod123 mod123){
		Mod123ExcelAction action = new Mod123ExcelAction(mod123);
		action.initialize(mod123.getModel().getName(mod123.getAdministration(), mod123.getPeriod()));
		ByteArrayOutputStream output = new ByteArrayOutputStream();			
		for (IModelScript<Mod123Key> ms : Model123ScriptProvider.obtainScript(mod123)) {
			action.accept(ms);
		}
		action.beforeFinalize();
		try {
			action.finalize(output);
		} catch (IOException e) {e.printStackTrace();}
		
		return new Attach(new Domain().setId(mod123.getDomain()).setName(domainName)
				, mod123.getId(), AttachType.MOD123, "Modelo 123", MimeType.MS_EXCEL, null, output.toByteArray());
	}

}
