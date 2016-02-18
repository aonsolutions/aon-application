package com.esferalia.aon.gwt.fiscal.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
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
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod115KeyInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FiscalServiceAsyncDecorator implements FiscalServiceAsync {

	private FiscalServiceAsync fsa;

	public FiscalServiceAsyncDecorator(FiscalServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// -------------------------------------------------------------- COMMON
	@Override
	public void mathExpression(String expression, AsyncCallback<Double> callback) {
		AON.start();
		fsa.mathExpression(expression, new AsyncCallbackWrapper<Double>(
				callback));
	}

	// -------------------------------------------------------------- PARAMS
	@Override
	public void getFiscalParameters(String domainName, int domain,
			AsyncCallback<FiscalParameters> callback) {
		AON.start();
		fsa.getFiscalParameters(domainName, domain,
				new AsyncCallbackWrapper<FiscalParameters>(callback));
	}

	// ------------------------------------------------------- FISCAL PANEL
	@Override
	public void getFiscalPanel(String domainName, int domain, int year,
			AsyncCallback<FiscalModelMatrix> callback) {
		AON.start();
		fsa.getFiscalPanel(domainName, domain, year,
				new AsyncCallbackWrapper<FiscalModelMatrix>(callback));
	}

	@Override
	public void getAllModels(String domainName, int domain,
			AsyncCallback<LinkedList<IFiscalModel>> callback) {
		AON.start();
		fsa.getAllModels(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<IFiscalModel>>(callback));
	}

	@Override
	public void getAllModels(String domainName, int domain, int year,
			AsyncCallback<LinkedList<IFiscalModel>> callback) {
		AON.start();
		fsa.getAllModels(domainName, domain, year,
				new AsyncCallbackWrapper<LinkedList<IFiscalModel>>(callback));
	}

	// -------------------------------------------------------------- ACTIVITIES
	@Override
	public void getActivities(int activityGroup,
			AsyncCallback<LinkedList<Activity>> callback) {
		AON.start();
		fsa.getActivities(activityGroup,
				new AsyncCallbackWrapper<LinkedList<Activity>>(callback));
	}

	// ---------------------------------------------------------------MODELO 190
	@Override
	public void deleteMod190(String domainName, int domain, Mod190 mod190,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod190(domainName, domain, mod190,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod190(String domainName, int domain, Mod190 mod190,
			AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.saveMod190(domainName, domain, mod190,
				new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void getMod190s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod190>> callback) {
		AON.start();
		fsa.getMod190s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod190>>(callback));
	}

	@Override
	public void initializeMod190(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.initializeMod190(domainName, domain, year,
				new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void getMod190(String domainName, int domain, Integer id,
			AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.getMod190(domainName, domain, id, new AsyncCallbackWrapper<Mod190>(
				callback));
	}

	@Override
	public void getMod190Detail(String domainName, int domain, Integer id,
			AsyncCallback<Mod190Detail> callback) {
		AON.start();
		fsa.getMod190Detail(domainName, domain, id,
				new AsyncCallbackWrapper<Mod190Detail>(callback));
	}

	// ---------------------------------------------------------------MODELO 193
	@Override
	public void deleteMod193(String domainName, int domain, Mod193 mod193,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod193(domainName, domain, mod193,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod193(String domainName, int domain, Mod193 mod193,
			AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.saveMod193(domainName, domain, mod193,
				new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void getMod193s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod193>> callback) {
		AON.start();
		fsa.getMod193s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod193>>(callback));
	}

	@Override
	public void initializeMod193(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.initializeMod193(domainName, domain, year,
				new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void getMod193(String domainName, int domain, Integer id,
			AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.getMod193(domainName, domain, id, new AsyncCallbackWrapper<Mod193>(
				callback));
	}

	// ---------------------------------------------------------------MODELO 180
	@Override
	public void deleteMod180(String domainName, int domainId, Mod180 mod180,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod180(domainName, domainId, mod180,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod180(String domainName, int domainId, Mod180 mod180,
			AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.saveMod180(domainName, domainId, mod180,
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180s(String domainName, int domainId,
			AsyncCallback<LinkedList<Mod180>> callback) {
		AON.start();
		fsa.getMod180s(domainName, domainId,
				new AsyncCallbackWrapper<LinkedList<Mod180>>(callback));
	}

	@Override
	public void initializeMod180(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.initializeMod180(domainName, domain, year,
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180(String domainName, int domainId, Integer id,
			AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.getMod180(domainName, domainId, id,
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180Detail(String domainName, int domainId, Integer id,
			AsyncCallback<Mod180Detail> callback) {
		AON.start();
		fsa.getMod180Detail(domainName, domainId, id,
				new AsyncCallbackWrapper<Mod180Detail>(callback));
	}

	// ---------------------------------------------------------------MODELO 184
	@Override
	public void deleteMod184(String domainName, int domain, Mod184 mod184,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod184(domainName, domain, mod184,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod184(String domainName, int domain, Mod184 mod184,
			AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.saveMod184(domainName, domain, mod184,
				new AsyncCallbackWrapper<Mod184>(callback));
	}

	@Override
	public void getMod184s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod184>> callback) {
		AON.start();
		fsa.getMod184s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod184>>(callback));
	}

	@Override
	public void initializeMod184(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.initializeMod184(domainName, domain, year,
				new AsyncCallbackWrapper<Mod184>(callback));
	}

	@Override
	public void getMod184(String domainName, int domain, Integer id,
			AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.getMod184(domainName, domain, id, new AsyncCallbackWrapper<Mod184>(
				callback));
	}

	// ---------------------------------------------------------------MODELO 390
	@Override
	public void getMod390s(String domainName, Integer domain,
			AsyncCallback<LinkedList<Mod390>> callback) {
		AON.start();
		fsa.getMod390s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod390>>(callback));
	}
	
	// ---------------------------------------------------------------MODELO 390 - 2014
	@Override
	public void getMod3902014(String domainName, Integer domain, Integer id,
			AsyncCallback<Mod3902014> callback) {
		AON.start();
		fsa.getMod3902014(domainName, domain, id, new AsyncCallbackWrapper<Mod3902014>(
				callback));
	}

	@Override
	public void saveMod3902014(String domainName, Integer domain, Mod3902014 mod390,
			AsyncCallback<Mod3902014> callback) {
		AON.start();
		fsa.saveMod3902014(domainName, domain, mod390,
				new AsyncCallbackWrapper<Mod3902014>(callback));
	}

	@Override
	public void deleteMod3902014(String domainName, Integer domain, Mod3902014 mod390,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod3902014(domainName, domain, mod390,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void initializeMod3902014(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod3902014> callback) {
		AON.start();
		fsa.initializeMod3902014(domainName, domain, year,
				new AsyncCallbackWrapper<Mod3902014>(callback));
	}

	// ---------------------------------------------------------------MODELO 390 - 2015

	@Override
	public void getMod3902015(String domainName, Integer domain, Integer id,
			AsyncCallback<Mod3902015> callback) {
		AON.start();
		fsa.getMod3902015(domainName, domain, id, new AsyncCallbackWrapper<Mod3902015>(
				callback));
	}

	@Override
	public void saveMod3902015(String domainName, Integer domain, Mod3902015 mod390,
			AsyncCallback<Mod3902015> callback) {
		AON.start();
		fsa.saveMod3902015(domainName, domain, mod390,
				new AsyncCallbackWrapper<Mod3902015>(callback));
	}

	@Override
	public void deleteMod3902015(String domainName, Integer domain, Mod3902015 mod390,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod3902015(domainName, domain, mod390,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void initializeMod3902015(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod3902015> callback) {
		AON.start();
		fsa.initializeMod3902015(domainName, domain, year,
				new AsyncCallbackWrapper<Mod3902015>(callback));
	}
	// ---------------------------------------------------------------MODELO 111

	@Override
	public void getMod111(String domainName, int domain, int id,
			AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.getMod111(domainName, domain, id, new AsyncCallbackWrapper<Mod111>(
				callback));
	}

	@Override
	public void getMod111s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod111>> callback) {
		AON.start();
		fsa.getMod111s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod111>>(callback));
	}

	@Override
	public void calculateMod111(String domainName, Mod111 mod111,
			AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.calculateMod111(domainName, mod111,
				new AsyncCallbackWrapper<Mod111>(callback));
	}

	@Override
	public void saveMod111(String domainName, Mod111 mod111,
			AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.saveMod111(domainName, mod111, new AsyncCallbackWrapper<Mod111>(
				callback));
	}
	
	@Override
	public void saveCommentsMod111(String domainName, Mod111 mod111,
			AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.saveCommentsMod111(domainName, mod111, new AsyncCallbackWrapper<Mod111>(
				callback));
	}

	@Override
	public void initializeForFinishMod111(String domainName, Mod111 mod111,
			AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.initializeForFinishMod111(domainName, mod111, new AsyncCallbackWrapper<Mod111>(
				callback));
	}
	
	@Override
	public void finishMod111(String domainName, Mod111 mod111,
			AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.finishMod111(domainName, mod111, new AsyncCallbackWrapper<Mod111>(
				callback));
	}
	
	@Override
	public void reopenMod111(String domainName, Mod111 mod111,
			AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.reopenMod111(domainName, mod111, new AsyncCallbackWrapper<Mod111>(
				callback));
	}

	@Override
	public void initializeMod111(String domainName, int currentDomain,
			Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.initializeMod111(domainName, currentDomain,mod111,new AsyncCallbackWrapper<Mod111>(callback));

	}

	@Override
	public void createMod111(String domainName, int currentDomain,
			Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.createMod111(domainName, currentDomain, mod111,new AsyncCallbackWrapper<Mod111>(callback));

	}

	@Override
	public void deleteMod111(String domainName, Mod111 mod111,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod111(domainName, mod111, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void getInfo(String domainName, int domain, Mod111 mod111, Mod111Key key, Mod111KeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod111, key, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}
	
	// ---------------------------------------------------------------MODELO 115

	@Override
	public void getMod115(String domainName, int domain, int id,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.getMod115(domainName, domain, id, new AsyncCallbackWrapper<Mod115>(
				callback));
	}

	@Override
	public void getMod115s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod115>> callback) {
		AON.start();
		fsa.getMod115s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod115>>(callback));
	}

	@Override
	public void calculateMod115(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.calculateMod115(domainName, mod115,
				new AsyncCallbackWrapper<Mod115>(callback));
	}

	@Override
	public void saveMod115(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.saveMod115(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}
	
	@Override
	public void saveCommentsMod115(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.saveCommentsMod115(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}

	@Override
	public void initializeForFinishMod115(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.initializeForFinishMod115(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}
	
	@Override
	public void finishMod115(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.finishMod115(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}
	
	@Override
	public void reopenMod115(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.reopenMod115(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}

	@Override
	public void initializeMod115(String domainName, int currentDomain,
			Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.initializeMod115(domainName, currentDomain,mod115,new AsyncCallbackWrapper<Mod115>(callback));

	}

	@Override
	public void createMod115(String domainName, int currentDomain,
			Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.createMod115(domainName, currentDomain, mod115,new AsyncCallbackWrapper<Mod115>(callback));

	}

	@Override
	public void deleteMod115(String domainName, Mod115 mod115,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod115(domainName, mod115, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void getInfo(String domainName, int domain, Mod115 mod115, Mod115Key key, Mod115KeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod115, key, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}

	// ---------------------------------------------------------------MODELO 202

	@Override
	public void getMod202(String domainName, int domain, int id,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.getMod202(domainName, domain, id, new AsyncCallbackWrapper<Mod202>(
				callback));
	}

	@Override
	public void getMod202s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod202>> callback) {
		AON.start();
		fsa.getMod202s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod202>>(callback));
	}

	@Override
	public void calculateMod202(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.calculateMod202(domainName, mod202,
				new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void saveMod202(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.saveMod202(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}

	@Override
	public void initializeMod202(String domainName, int currentDomain,
			Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.initializeMod202(domainName, currentDomain, mod202,
				new AsyncCallbackWrapper<Mod202>(callback));

	}

	@Override
	public void deleteMod202(String domainName, Mod202 mod202,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod202(domainName, mod202, new AsyncCallbackWrapper<Void>(
				callback));
	}

	// ---------------------------------------------------------------MODELO 200
	// - 2013

	@Override
	public void initializeNewMod2002013(String domainName, int domain,
			Mod2002013 mod200, AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.initializeNewMod2002013(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002013>(callback));
	}

	@Override
	public void initializeMod2002013(String domainName, int domain,
			Mod2002013 mod200, AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.initializeMod2002013(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002013>(callback));
	}

	@Override
	public void getMod2002013ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.getMod2002013ByYear(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002013>(callback));
	}

	@Override
	public void calculateMod2002013(Mod2002013 mod200,
			AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.calculateMod2002013(mod200, new AsyncCallbackWrapper<Mod2002013>(
				callback));
	}

	@Override
	public void deleteMod2002013(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002013(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002013(Mod2002013 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002013(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002013ById(String domainName, int domain, int id,
			AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.getMod2002013ById(domainName, domain, id,
				new AsyncCallbackWrapper<Mod2002013>(callback));
	}

	@Override
	public void saveMod2002013(String domainName, int domain,
			Mod2002013 mod200, AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.saveMod2002013(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002013>(callback));

	}

	@Override
	public void validateMod2002013(Mod2002013 mod200,
			AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.validateMod2002013(mod200, new AsyncCallbackWrapper<Mod2002013>(
				callback));
	}

	// ---------------------------------------------------------------MODELO 200
	// - 2014

	@Override
	public void createMod2002014(String domainName, int domain, int year,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.createMod2002014(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void initializeNewMod2002014(String domainName, int domain,
			Mod2002014 mod200, AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.initializeNewMod2002014(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void initializeMod2002014(String domainName, int domain,
			Mod2002014 mod200, AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.initializeMod2002014(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void getMod2002014ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.getMod2002014ByYear(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void calculateMod2002014(Mod2002014 mod200,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.calculateMod2002014(mod200, new AsyncCallbackWrapper<Mod2002014>(
				callback));
	}

	@Override
	public void deleteMod2002014(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002014(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002014(Mod2002014 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002014(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002014ById(String domainName, int domain, int id,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.getMod2002014ById(domainName, domain, id,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void saveMod2002014(String domainName, int domain,
			Mod2002014 mod200, AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.saveMod2002014(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002014>(callback));

	}

	@Override
	public void validateMod2002014(Mod2002014 mod200,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.validateMod2002014(mod200, new AsyncCallbackWrapper<Mod2002014>(
				callback));
	}

	@Override
	public void importMod2002013(String domainName, int domain,
			Mod2002014 mod200, AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.importMod2002013(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void fillMod2002014AccountingData(Mod2002014 mod200,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.fillMod2002014AccountingData(mod200,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	// ---------------------------------------------------------------
	// NORMALIZED MEMORY
	@Override
	public void readMemory(Memory memory, AsyncCallback<Memory> callback) {
		AON.start();
		fsa.readMemory(memory, new AsyncCallbackWrapper<Memory>(callback));
	}

	@Override
	public void saveMemory(Memory memory, AsyncCallback<Memory> callback) {
		AON.start();
		fsa.saveMemory(memory, new AsyncCallbackWrapper<Memory>(callback));
	}

	@Override
	public void deleteMemory(Memory memory, AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMemory(memory, new AsyncCallbackWrapper<Void>(callback));
	}

	// --------------------------------------------------------------- ACCOUNT
	// PERIOD
	@Override
	public void getDomainPeriods(String domainName, int domain,
			AsyncCallback<LinkedList<AccountPeriod>> callback) {
		AON.start();
		fsa.getDomainPeriods(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<AccountPeriod>>(callback));
	}

	// --------------------------------------------------------------- ACCOUNT
	// ENTRIES
	@Override
	public void getAccountEntries(String domainName, int domain,
			AccountEntryParams params, int offset, int limit,
			AsyncCallback<LinkedList<AccountEntry>> callback) {
		AON.start();
		fsa.getAccountEntries(domainName, domain, params, offset, limit,
				new AsyncCallbackWrapper<LinkedList<AccountEntry>>(callback));
	}

	@Override
	public void getAccountEntry(String domainName, int domain, int id,
			AsyncCallback<AccountEntry> callback) {
		AON.start();
		fsa.getAccountEntry(domainName, domain, id,
				new AsyncCallbackWrapper<AccountEntry>(callback));
	}

	@Override
	public void save(String domainName, int domain, AccountEntry ae,
			AsyncCallback<AccountEntry> callback) {
		AON.start();
		fsa.save(domainName, domain, ae,
				new AsyncCallbackWrapper<AccountEntry>(callback));
	}

	@Override
	public void insertSalaryAccountEntries(String domainName, int domain,
			Date from, Date to, String concept, Integer registryBank,
			AsyncCallback<LinkedList<AccountEntry>> callback) {
		AON.start();
		fsa.insertSalaryAccountEntries(domainName, domain, from, to, concept,
				registryBank,
				new AsyncCallbackWrapper<LinkedList<AccountEntry>>(callback));
	}

	@Override
	public void getSalaryAccountEntries(String domainName, int domain,
			Date from, Date to, AsyncCallback<LinkedList<AccountEntry>> callback) {
		AON.start();
		fsa.getSalaryAccountEntries(domainName, domain, from, to,
				new AsyncCallbackWrapper<LinkedList<AccountEntry>>(callback));
	}

	@Override
	public void deleteAccountEntry(String domainName, int domain, Integer id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteAccountEntry(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	// --------------------------------------------------------------- ACCOUNT
	// STATEMENT
	@Override
	public void getAccountStatement(String domainName, int domain,
			AccountStatementParams params,
			AsyncCallback<AccountStatementReport> callback) {
		AON.start();
		fsa.getAccountStatement(domainName, domain, params,
				new AsyncCallbackWrapper<AccountStatementReport>(callback));
	}

	@Override
	public void getAccountBalance(String domainName, int domain,
			AccountStatementParams params,
			AsyncCallback<LinkedList<AccountStatement>> callback) {
		AON.start();
		fsa.getAccountBalance(
				domainName,
				domain,
				params,
				new AsyncCallbackWrapper<LinkedList<AccountStatement>>(callback));
	}

}
