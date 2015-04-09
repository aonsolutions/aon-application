package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
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
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FiscalServiceAsyncDecorator implements FiscalServiceAsync {

	private FiscalServiceAsync fiscalServiceAsync;

	public FiscalServiceAsyncDecorator(FiscalServiceAsync mod190ServiceAsync) {
		this.fiscalServiceAsync = mod190ServiceAsync;
	}

	// -------------------------------------------------------------- PARAMS
	@Override
	public void getFiscalParameters(String domainName,int domain, AsyncCallback<FiscalParameters> callback) {
		AON.start();
		fiscalServiceAsync.getFiscalParameters(domainName,domain, 
				new AsyncCallbackWrapper<FiscalParameters>(callback));
	}

	// ------------------------------------------------------- FISCAL PANEL
	@Override
	public void getFiscalPanel(String domainName, int domain,int year,
			AsyncCallback<FiscalModelMatrix> callback) {
		AON.start();
		fiscalServiceAsync.getFiscalPanel(domainName,domain,year, 
				new AsyncCallbackWrapper<FiscalModelMatrix>(callback));
	}
	// ------------------------------------------------------- FISCAL ACTIVITIES
	@Override
	public void calculate(String domainName, FiscalActivity fa,AsyncCallback<FiscalActivity> callback) {
		AON.start();
		fiscalServiceAsync.calculate(domainName,fa, 
				new AsyncCallbackWrapper<FiscalActivity>(callback));
		
	}
	@Override
	public void getModuleEpigraphs(int year, AsyncCallback<ArrayList<Epigraph>> callback) {
		AON.start();
		fiscalServiceAsync.getModuleEpigraphs(year, 
				new AsyncCallbackWrapper<ArrayList<Epigraph>>(callback));
	}
	@Override
	public void getFiscalActivities(String domainName, int domain,
			AsyncCallback<ArrayList<FiscalActivity>> callback) {
		AON.start();
		fiscalServiceAsync.getFiscalActivities(domainName,domain, 
				new AsyncCallbackWrapper<ArrayList<FiscalActivity>>(callback));
	}
	@Override
	public void getFiscalActivity(String domainName, int domain, int id,
			AsyncCallback<FiscalActivity> callback) {
		AON.start();
		fiscalServiceAsync.getFiscalActivity(domainName,domain,id, 
				new AsyncCallbackWrapper<FiscalActivity>(callback));
	}
	@Override
	public void getFiscalActivityFor(String domainName, Epigraph epigraph
			, FiscalActivity fa,AsyncCallback<FiscalActivity> callback) {
		AON.start();
		fiscalServiceAsync.getFiscalActivityFor(domainName, epigraph, fa,  
				new AsyncCallbackWrapper<FiscalActivity>(callback));
	}
	@Override
	public void save(String domainName, FiscalActivity fa,
			AsyncCallback<FiscalActivity> callback) {
		AON.start();
		fiscalServiceAsync.save(domainName, fa, 
				new AsyncCallbackWrapper<FiscalActivity>(callback));
		
	}

	@Override
	public void delete(String domainName, FiscalActivity fa,
			AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.delete(domainName, fa, 
				new AsyncCallbackWrapper<Void>(callback));
	}
	
	// ----------------------------------------------------FISCAL MODEL
	@Override
	public void getFiscalModels(String domainName, int domain,
			AsyncCallback<LinkedList<FiscalModel>> callback) {
		AON.start();
		fiscalServiceAsync.getFiscalModels(domainName, domain, 
				new AsyncCallbackWrapper<LinkedList<FiscalModel>>(callback));
	}

	@Override
	public void getFiscalModel(String domainName, int domain, int id,
			AsyncCallback<FiscalModel> callback) {
		AON.start();
		fiscalServiceAsync.getFiscalModel(domainName, domain,id, 
				new AsyncCallbackWrapper<FiscalModel>(callback));
	}

	@Override
	public void save(String domainName, FiscalModel fm,
			AsyncCallback<FiscalModel> callback) {
		AON.start();
		fiscalServiceAsync.save(domainName, fm, 
				new AsyncCallbackWrapper<FiscalModel>(callback));
	}

	@Override
	public void delete(String domainName, FiscalModel fm,
			AsyncCallback<Void> callback) {
		fiscalServiceAsync.delete(domainName, fm, 
				new AsyncCallbackWrapper<Void>(callback));
	}

	// -------------------------------------------------------------- ACTIVITIES
	@Override
	public void getActivities(int activityGroup,
			AsyncCallback<ArrayList<Activity>> callback) {
		AON.start();
		fiscalServiceAsync.getActivities(activityGroup,
				new AsyncCallbackWrapper<ArrayList<Activity>>(callback));
	}

	// ---------------------------------------------------------------MODELO 190
	@Override
	public void deleteMod190(String domainName, int domain, Mod190 mod190,
			AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.deleteMod190(domainName, domain, mod190,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod190(String domainName, int domain, Mod190 mod190,
			AsyncCallback<Mod190> callback) {
		AON.start();
		fiscalServiceAsync.saveMod190(domainName, domain, mod190,
				new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void getMod190s(String domainName, int domain,
			AsyncCallback<ArrayList<Mod190>> callback) {
		AON.start();
		fiscalServiceAsync.getMod190s(domainName, domain,
				new AsyncCallbackWrapper<ArrayList<Mod190>>(callback));
	}
	@Override
	public void initializeMod190(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod190> callback) {
		AON.start();
		fiscalServiceAsync.initializeMod190(domainName, domain, year, 
				new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void getMod190(String domainName, int domain, Integer id,
			AsyncCallback<Mod190> callback) {
		AON.start();
		fiscalServiceAsync.getMod190(domainName, domain, id,
				new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void getMod190Detail(String domainName, int domain, Integer id,
			AsyncCallback<Mod190Detail> callback) {
		AON.start();
		fiscalServiceAsync.getMod190Detail(domainName, domain, id,
				new AsyncCallbackWrapper<Mod190Detail>(callback));
	}

	// ---------------------------------------------------------------MODELO 193
	@Override
	public void deleteMod193(String domainName, int domain, Mod193 mod193,
			AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.deleteMod193(domainName, domain, mod193,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod193(String domainName, int domain, Mod193 mod193,
			AsyncCallback<Mod193> callback) {
		AON.start();
		fiscalServiceAsync.saveMod193(domainName, domain, mod193,
				new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void getMod193s(String domainName, int domain,
			AsyncCallback<ArrayList<Mod193>> callback) {
		AON.start();
		fiscalServiceAsync.getMod193s(domainName, domain,
				new AsyncCallbackWrapper<ArrayList<Mod193>>(callback));
	}
	@Override
	public void initializeMod193(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod193> callback) {
		AON.start();
		fiscalServiceAsync.initializeMod193(domainName, domain, year, 
				new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void getMod193(String domainName, int domain, Integer id,
			AsyncCallback<Mod193> callback) {
		AON.start();
		fiscalServiceAsync.getMod193(domainName, domain, id,
				new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void getMod193Detail(String domainName, int domain, Integer id,
			AsyncCallback<Mod193Detail> callback) {
		AON.start();
		fiscalServiceAsync.getMod193Detail(domainName, domain, id,
				new AsyncCallbackWrapper<Mod193Detail>(callback));
	}

	// ---------------------------------------------------------------MODELO 180
	@Override
	public void deleteMod180(String domainName, int domainId, Mod180 mod180,
			AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.deleteMod180(domainName, domainId, mod180,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod180(String domainName, int domainId, Mod180 mod180,
			AsyncCallback<Mod180> callback) {
		AON.start();
		fiscalServiceAsync.saveMod180(domainName, domainId, mod180,
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180s(String domainName, int domainId,
			AsyncCallback<ArrayList<Mod180>> callback) {
		AON.start();
		fiscalServiceAsync.getMod180s(domainName, domainId,
				new AsyncCallbackWrapper<ArrayList<Mod180>>(callback));
	}

	@Override
	public void initializeMod180(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod180> callback) {
		AON.start();
		fiscalServiceAsync.initializeMod180(domainName, domain, year, 
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180(String domainName, int domainId, Integer id,
			AsyncCallback<Mod180> callback) {
		AON.start();
		fiscalServiceAsync.getMod180(domainName, domainId, id,
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180Detail(String domainName, int domainId, Integer id,
			AsyncCallback<Mod180Detail> callback) {
		AON.start();
		fiscalServiceAsync.getMod180Detail(domainName, domainId, id,
				new AsyncCallbackWrapper<Mod180Detail>(callback));
	}

	// ---------------------------------------------------------------MODELO 184
	@Override
	public void deleteMod184(String domainName, int domain, Mod184 mod184,
			AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.deleteMod184(domainName, domain, mod184,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod184(String domainName, int domain, Mod184 mod184,
			AsyncCallback<Mod184> callback) {
		AON.start();
		fiscalServiceAsync.saveMod184(domainName, domain, mod184,
				new AsyncCallbackWrapper<Mod184>(callback));
	}

	@Override
	public void getMod184s(String domainName, int domain,
			AsyncCallback<ArrayList<Mod184>> callback) {
		AON.start();
		fiscalServiceAsync.getMod184s(domainName, domain,
				new AsyncCallbackWrapper<ArrayList<Mod184>>(callback));
	}
	@Override
	public void initializeMod184(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod184> callback) {
		AON.start();
		fiscalServiceAsync.initializeMod184(domainName, domain, year, 
				new AsyncCallbackWrapper<Mod184>(callback));
	}

	@Override
	public void getMod184(String domainName, int domain, Integer id,
			AsyncCallback<Mod184> callback) {
		AON.start();
		fiscalServiceAsync.getMod184(domainName, domain, id,
				new AsyncCallbackWrapper<Mod184>(callback));
	}

	// ---------------------------------------------------------------MODELO 390
	@Override
	public void getMod390(String domainName, Integer domain, Integer id,
			AsyncCallback<Mod390> callback) {
		AON.start();
		fiscalServiceAsync.getMod390(domainName, domain, id,
				new AsyncCallbackWrapper<Mod390>(callback));
	}

	@Override
	public void getMod390s(String domainName, Integer domain,
			AsyncCallback<ArrayList<Mod390>> callback) {
		AON.start();
		fiscalServiceAsync.getMod390s(domainName, domain,
				new AsyncCallbackWrapper<ArrayList<Mod390>>(callback));
	}

	@Override
	public void saveMod390(String domainName, Integer domain, Mod390 mod390,
			AsyncCallback<Mod390> callback) {
		AON.start();
		fiscalServiceAsync.saveMod390(domainName, domain, mod390,
				new AsyncCallbackWrapper<Mod390>(callback));
	}

	@Override
	public void deleteMod390(String domainName, Integer domain, Mod390 mod390,
			AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.deleteMod390(domainName, domain, mod390,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getMod390Details(String domainName, Integer domain, Mod390 mod390,
			AsyncCallback<ArrayList<Mod390Detail>> callback) {
		AON.start();
		fiscalServiceAsync.getMod390Details(domainName,domain, mod390,
				new AsyncCallbackWrapper<ArrayList<Mod390Detail>>(callback));
	}

	@Override
	public void initializeMod390(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod390> callback) {
		AON.start();
		fiscalServiceAsync.initializeMod390(domainName, domain, year, 
				new AsyncCallbackWrapper<Mod390>(callback));
	}
	// ---------------------------------------------------------------MODELO 131

	@Override
	public void getMod131(String domainName, int domain, int id,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fiscalServiceAsync.getMod131(domainName, domain,id, 
						new AsyncCallbackWrapper<Mod131>(callback));
	}

	@Override
	public void getMod131s(String domainName, int domain, 
			AsyncCallback<LinkedList<Mod131>> callback) {
		AON.start();
		fiscalServiceAsync.getMod131s(domainName, domain, 
						new AsyncCallbackWrapper<LinkedList<Mod131>>(callback));
	}

	@Override
	public void calculateMod131(String domainName, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fiscalServiceAsync.calculateMod131(domainName, mod131, 
						new AsyncCallbackWrapper<Mod131>(callback));
	}
	@Override
	public void saveMod131(String domainName, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fiscalServiceAsync.saveMod131(domainName, mod131, 
				new AsyncCallbackWrapper<Mod131>(callback));
	}

	// ---------------------------------------------------------------MODELO 202

	@Override
	public void getMod202(String domainName, int domain, int id,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fiscalServiceAsync.getMod202(domainName, domain,id, 
						new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void getMod202s(String domainName, int domain, 
			AsyncCallback<LinkedList<Mod202>> callback) {
		AON.start();
		fiscalServiceAsync.getMod202s(domainName, domain, 
						new AsyncCallbackWrapper<LinkedList<Mod202>>(callback));
	}

	@Override
	public void calculateMod202(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fiscalServiceAsync.calculateMod202(domainName, mod202, 
						new AsyncCallbackWrapper<Mod202>(callback));
	}
	@Override
	public void saveMod202(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fiscalServiceAsync.saveMod202(domainName, mod202, 
				new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void initializeMod202(String currentDomainName, int currentDomain,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fiscalServiceAsync.initializeMod202(currentDomainName, currentDomain, 
				new AsyncCallbackWrapper<Mod202>(callback));
		
	}

	@Override
	public void deleteMod202(String currentDomainName, Mod202 mod202,
			AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.deleteMod202(currentDomainName,mod202,  
				new AsyncCallbackWrapper<Void>(callback));
	}
}
