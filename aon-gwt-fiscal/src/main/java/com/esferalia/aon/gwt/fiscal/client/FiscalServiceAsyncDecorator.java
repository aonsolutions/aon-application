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
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.finance.Finance;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
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
	public void getInfo(String domainName, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod111, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
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
	public void getInfo(String domainName, int domain, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod115, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}

	// ---------------------------------------------------------------MODELO 123

	@Override
	public void getMod123(String domainName, int domain, int id,
			AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.getMod123(domainName, domain, id, new AsyncCallbackWrapper<Mod123>(
				callback));
	}

	@Override
	public void getMod123s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod123>> callback) {
		AON.start();
		fsa.getMod123s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod123>>(callback));
	}

	@Override
	public void calculateMod123(String domainName, Mod123 mod123,
			AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.calculateMod123(domainName, mod123,
				new AsyncCallbackWrapper<Mod123>(callback));
	}

	@Override
	public void saveMod123(String domainName, Mod123 mod123,
			AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.saveMod123(domainName, mod123, new AsyncCallbackWrapper<Mod123>(
				callback));
	}
	
	@Override
	public void saveCommentsMod123(String domainName, Mod123 mod123,
			AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.saveCommentsMod123(domainName, mod123, new AsyncCallbackWrapper<Mod123>(
				callback));
	}

	@Override
	public void initializeForFinishMod123(String domainName, Mod123 mod123,
			AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.initializeForFinishMod123(domainName, mod123, new AsyncCallbackWrapper<Mod123>(
				callback));
	}
	
	@Override
	public void finishMod123(String domainName, Mod123 mod123,
			AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.finishMod123(domainName, mod123, new AsyncCallbackWrapper<Mod123>(
				callback));
	}
	
	@Override
	public void reopenMod123(String domainName, Mod123 mod123,
			AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.reopenMod123(domainName, mod123, new AsyncCallbackWrapper<Mod123>(
				callback));
	}

	@Override
	public void initializeMod123(String domainName, int currentDomain,
			Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.initializeMod123(domainName, currentDomain,mod123,new AsyncCallbackWrapper<Mod123>(callback));

	}

	@Override
	public void createMod123(String domainName, int currentDomain,
			Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.createMod123(domainName, currentDomain, mod123,new AsyncCallbackWrapper<Mod123>(callback));

	}

	@Override
	public void deleteMod123(String domainName, Mod123 mod123,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod123(domainName, mod123, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void getInfo(String domainName, int domain, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod123, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}

	// ---------------------------------------------------------------MODELO 130

	@Override
	public void getMod130(String domainName, int domain, int id,
			AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.getMod130(domainName, domain, id, new AsyncCallbackWrapper<Mod130>(
				callback));
	}

	@Override
	public void getMod130s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod130>> callback) {
		AON.start();
		fsa.getMod130s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod130>>(callback));
	}

	@Override
	public void calculateMod130(String domainName, Mod130 mod130,
			AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.calculateMod130(domainName, mod130,
				new AsyncCallbackWrapper<Mod130>(callback));
	}

	@Override
	public void saveMod130(String domainName, Mod130 mod130,
			AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.saveMod130(domainName, mod130, new AsyncCallbackWrapper<Mod130>(
				callback));
	}
	
	@Override
	public void saveCommentsMod130(String domainName, Mod130 mod130,
			AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.saveCommentsMod130(domainName, mod130, new AsyncCallbackWrapper<Mod130>(
				callback));
	}

	@Override
	public void initializeForFinishMod130(String domainName, Mod130 mod130,
			AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.initializeForFinishMod130(domainName, mod130, new AsyncCallbackWrapper<Mod130>(
				callback));
	}
	
	@Override
	public void finishMod130(String domainName, Mod130 mod130,
			AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.finishMod130(domainName, mod130, new AsyncCallbackWrapper<Mod130>(
				callback));
	}
	
	@Override
	public void reopenMod130(String domainName, Mod130 mod130,
			AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.reopenMod130(domainName, mod130, new AsyncCallbackWrapper<Mod130>(
				callback));
	}

	@Override
	public void initializeMod130(String domainName, int currentDomain,
			Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.initializeMod130(domainName, currentDomain,mod130,new AsyncCallbackWrapper<Mod130>(callback));

	}

	@Override
	public void createMod130(String domainName, int currentDomain,
			Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.createMod130(domainName, currentDomain, mod130,new AsyncCallbackWrapper<Mod130>(callback));

	}

	@Override
	public void deleteMod130(String domainName, Mod130 mod130,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod130(domainName, mod130, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void getInfo(String domainName, int domain, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod130, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}
	
	// ---------------------------------------------------------------MODELO 131

	@Override
	public void getMod131(String domainName, int domain, int id,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.getMod131(domainName, domain, id, new AsyncCallbackWrapper<Mod131>(
				callback));
	}

	@Override
	public void getMod131s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod131>> callback) {
		AON.start();
		fsa.getMod131s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod131>>(callback));
	}

	@Override
	public void calculateMod131(String domainName, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.calculateMod131(domainName, mod131,
				new AsyncCallbackWrapper<Mod131>(callback));
	}
	@Override
	public void calculateMod131Activity(String domainName, int domain,Mod131Activity activity, AsyncCallback<Mod131Activity> callback) {
		AON.start();
		fsa.calculateMod131Activity(domainName, domain, activity,
				new AsyncCallbackWrapper<Mod131Activity>(callback));
	}

	@Override
	public void saveMod131(String domainName, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.saveMod131(domainName, mod131, new AsyncCallbackWrapper<Mod131>(
				callback));
	}
	
	@Override
	public void saveCommentsMod131(String domainName, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.saveCommentsMod131(domainName, mod131, new AsyncCallbackWrapper<Mod131>(
				callback));
	}

	@Override
	public void initializeForFinishMod131(String domainName, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.initializeForFinishMod131(domainName, mod131, new AsyncCallbackWrapper<Mod131>(
				callback));
	}
	
	@Override
	public void finishMod131(String domainName, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.finishMod131(domainName, mod131, new AsyncCallbackWrapper<Mod131>(
				callback));
	}
	
	@Override
	public void reopenMod131(String domainName, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.reopenMod131(domainName, mod131, new AsyncCallbackWrapper<Mod131>(
				callback));
	}

	@Override
	public void initializeMod131(String domainName, int currentDomain,
			Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.initializeMod131(domainName, currentDomain,mod131,new AsyncCallbackWrapper<Mod131>(callback));

	}

	@Override
	public void createMod131(String domainName, int currentDomain,
			Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.createMod131(domainName, currentDomain, mod131,new AsyncCallbackWrapper<Mod131>(callback));

	}

	@Override
	public void deleteMod131(String domainName, Mod131 mod131,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod131(domainName, mod131, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void getInfo(String domainName, int domain, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod131, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
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
	@Override
	public void saveCommentsMod202(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.saveCommentsMod202(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}

	@Override
	public void initializeForFinishMod202(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.initializeForFinishMod202(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}
	
	@Override
	public void finishMod202(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.finishMod202(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}
	
	@Override
	public void reopenMod202(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.reopenMod202(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}
	@Override
	public void createMod202(String domainName, int currentDomain,
			Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.createMod202(domainName, currentDomain, mod202,new AsyncCallbackWrapper<Mod202>(callback));

	}
	@Override
	public void getInfo(String domainName, int domain, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod202, script, infoKey,new AsyncCallbackWrapper<String>(callback));
	}

	// ---------------------------------------------------------------MODELO 200
	@Override
	public void getMod200s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod200>> callback) {
		AON.start();
		fsa.getMod200s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod200>>(callback));
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
	public void previewSalaryAccountEntries(String domainName, int domain,
			Date from, Date to, String concept, Integer registryBank,
			AsyncCallback<LinkedList<AccountEntry>> callback) {
		AON.start();
		fsa.previewSalaryAccountEntries(domainName, domain, from, to, concept,
				registryBank,
				new AsyncCallbackWrapper<LinkedList<AccountEntry>>(callback));
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

	@Override
	public void initializeInvoice(String domainName, int domain,AccountingRegistry registry
		,Integer activity,Date issueDate,AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.initializeInvoice(domainName, domain, registry,activity,issueDate,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}
	
	@Override
	public void getAccountingInvoice(String domainName, int domain, Integer accountEntry,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getAccountingInvoice(domainName, domain, accountEntry,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}
	
	@Override
	public void getAccountingInvoiceFromInvoice(String domainName, int domain, Integer invoiceId,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getAccountingInvoiceFromInvoice(domainName, domain, invoiceId,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}
	

	@Override
	public void save(String domainName, int domain, AccountingInvoice invoice,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.save(domainName, domain, invoice,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}

	@Override
	public void getRegistryLastAccountingInvoice(String currentDomainName, int currentDomain, Integer registryId,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.getRegistryLastAccountingInvoice(currentDomainName,currentDomain, registryId,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}

	@Override
	public void rectifyInvoice(String currentDomainName, int currentDomain, Integer invoiceId, InvoiceRectificationData data,
			AsyncCallback<AccountingInvoice> callback) {
		AON.start();
		fsa.rectifyInvoice(currentDomainName,currentDomain, invoiceId, data,
				new AsyncCallbackWrapper<AccountingInvoice>(callback));
	}

	@Override
	public void getSalaryEntries(String domainName, int domain, Date from, Date to,
			AsyncCallback<LinkedList<SalaryEntry>> callback) {
		AON.start();
		fsa.getSalaryEntries(domainName,domain, from, to,
				new AsyncCallbackWrapper<LinkedList<SalaryEntry>>(callback));
	}
	
	@Override
	public void getSalaryFormatted(String domainName, int domain, Date from, Date to, AsyncCallback<String> callback) {
		AON.start();
		fsa.getSalaryFormatted(domainName,domain, from, to,
				new AsyncCallbackWrapper<String>(callback));
	}
	// --------------------------------------------------------------- VAT
	@Override
	public void getVatContext(String domainName, int domain, VatParams params,
			AsyncCallback<LinkedList<VatContext>> callback) {
		AON.start();
		fsa.getVatContext(domainName, domain, params, 
				new AsyncCallbackWrapper<LinkedList<VatContext>>(callback));
		
	}
	@Override
	public void getVatContextReport(String domainName, int domain, VatParams params,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getVatContextReport(domainName, domain, params, 
				new AsyncCallbackWrapper<String>(callback));
		
	}
	@Override
	public void getVatSummaryContext(String domainName, int domain, VatParams params,
			AsyncCallback<LinkedList<VatSummaryContext>> callback) {
		fsa.getVatSummaryContext(domainName, domain, params, 
				new AsyncCallbackWrapper<LinkedList<VatSummaryContext>>(callback));
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

	@Override
	public void getAccountFinances(String domainName, int domain, FinanceParams params, int offset, int limit,
			AsyncCallback<LinkedList<Finance>> callback) {
		AON.start();
		fsa.getAccountFinances(domainName, domain, params, offset, limit,
				new AsyncCallbackWrapper<LinkedList<Finance>>(callback));
	}

	@Override
	public void getFinanceEntry(String domainName, int domain, Integer accountEntry,
			AsyncCallback<FinanceEntry> callback) {
		AON.start();
		fsa.getFinanceEntry(domainName, domain, accountEntry,
				new AsyncCallbackWrapper<FinanceEntry>(callback));
	}

	@Override
	public void save(String domainName, int domain, FinanceEntry financeEntry,
			AsyncCallback<FinanceEntry> asyncCallback) {
		AON.start();
		fsa.save(domainName, domain, financeEntry,
				new AsyncCallbackWrapper<FinanceEntry>(asyncCallback));
	}




}
