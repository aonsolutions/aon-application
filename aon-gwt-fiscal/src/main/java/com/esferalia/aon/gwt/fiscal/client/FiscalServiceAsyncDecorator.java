package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.fiscal.shared.Memory;
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
import com.esferalia.aon.occam.api.model.fiscal.mod200.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FiscalServiceAsyncDecorator implements FiscalServiceAsync {

	private FiscalServiceAsync fsa;

	public FiscalServiceAsyncDecorator(FiscalServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// -------------------------------------------------------------- PARAMS
	@Override
	public void getFiscalParameters(String domainName,int domain, AsyncCallback<FiscalParameters> callback) {
		AON.start();
		fsa.getFiscalParameters(domainName,domain, 
				new AsyncCallbackWrapper<FiscalParameters>(callback));
	}

	// ------------------------------------------------------- FISCAL PANEL
	@Override
	public void getFiscalPanel(String domainName, int domain,int year,
			AsyncCallback<FiscalModelMatrix> callback) {
		AON.start();
		fsa.getFiscalPanel(domainName,domain,year, 
				new AsyncCallbackWrapper<FiscalModelMatrix>(callback));
	}
	@Override
	public void getAllModels(String domainName, int domain,
			AsyncCallback<LinkedList<IFiscalModel>> callback) {
		AON.start();
		fsa.getAllModels(domainName,domain, 
				new AsyncCallbackWrapper<LinkedList<IFiscalModel>>(callback));
	}
	@Override
	public void getAllModels(String domainName, int domain, int year,
			AsyncCallback<LinkedList<IFiscalModel>> callback) {
		AON.start();
		fsa.getAllModels(domainName,domain,year, 
				new AsyncCallbackWrapper<LinkedList<IFiscalModel>>(callback));
	}
	
	// ------------------------------------------------------- FISCAL ACTIVITIES
	@Override
	public void calculate(String domainName, FiscalActivity fa,AsyncCallback<FiscalActivity> callback) {
		AON.start();
		fsa.calculate(domainName,fa, 
				new AsyncCallbackWrapper<FiscalActivity>(callback));
		
	}
	@Override
	public void getModuleEpigraphs(int year, AsyncCallback<ArrayList<Epigraph>> callback) {
		AON.start();
		fsa.getModuleEpigraphs(year, 
				new AsyncCallbackWrapper<ArrayList<Epigraph>>(callback));
	}
	@Override
	public void getFiscalActivities(String domainName, int domain,
			AsyncCallback<ArrayList<FiscalActivity>> callback) {
		AON.start();
		fsa.getFiscalActivities(domainName,domain, 
				new AsyncCallbackWrapper<ArrayList<FiscalActivity>>(callback));
	}
	@Override
	public void getFiscalActivity(String domainName, int domain, int id,
			AsyncCallback<FiscalActivity> callback) {
		AON.start();
		fsa.getFiscalActivity(domainName,domain,id, 
				new AsyncCallbackWrapper<FiscalActivity>(callback));
	}
	@Override
	public void getFiscalActivityFor(String domainName, Epigraph epigraph
			, FiscalActivity fa,AsyncCallback<FiscalActivity> callback) {
		AON.start();
		fsa.getFiscalActivityFor(domainName, epigraph, fa,  
				new AsyncCallbackWrapper<FiscalActivity>(callback));
	}
	@Override
	public void save(String domainName, FiscalActivity fa,
			AsyncCallback<FiscalActivity> callback) {
		AON.start();
		fsa.save(domainName, fa, 
				new AsyncCallbackWrapper<FiscalActivity>(callback));
		
	}

	@Override
	public void delete(String domainName, FiscalActivity fa,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, fa, 
				new AsyncCallbackWrapper<Void>(callback));
	}
	
	// ----------------------------------------------------FISCAL MODEL
	@Override
	public void getFiscalModels(String domainName, int domain,
			AsyncCallback<LinkedList<FiscalModel>> callback) {
		AON.start();
		fsa.getFiscalModels(domainName, domain, 
				new AsyncCallbackWrapper<LinkedList<FiscalModel>>(callback));
	}

	@Override
	public void getFiscalModel(String domainName, int domain, int id,
			AsyncCallback<FiscalModel> callback) {
		AON.start();
		fsa.getFiscalModel(domainName, domain,id, 
				new AsyncCallbackWrapper<FiscalModel>(callback));
	}

	@Override
	public void save(String domainName, FiscalModel fm,
			AsyncCallback<FiscalModel> callback) {
		AON.start();
		fsa.save(domainName, fm, 
				new AsyncCallbackWrapper<FiscalModel>(callback));
	}

	@Override
	public void delete(String domainName, FiscalModel fm,
			AsyncCallback<Void> callback) {
		fsa.delete(domainName, fm, 
				new AsyncCallbackWrapper<Void>(callback));
	}

	// -------------------------------------------------------------- ACTIVITIES
	@Override
	public void getActivities(int activityGroup,
			AsyncCallback<ArrayList<Activity>> callback) {
		AON.start();
		fsa.getActivities(activityGroup,
				new AsyncCallbackWrapper<ArrayList<Activity>>(callback));
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
			AsyncCallback<ArrayList<Mod190>> callback) {
		AON.start();
		fsa.getMod190s(domainName, domain,
				new AsyncCallbackWrapper<ArrayList<Mod190>>(callback));
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
		fsa.getMod190(domainName, domain, id,
				new AsyncCallbackWrapper<Mod190>(callback));
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
			AsyncCallback<ArrayList<Mod193>> callback) {
		AON.start();
		fsa.getMod193s(domainName, domain,
				new AsyncCallbackWrapper<ArrayList<Mod193>>(callback));
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
		fsa.getMod193(domainName, domain, id,
				new AsyncCallbackWrapper<Mod193>(callback));
	}

	@Override
	public void getMod193Detail(String domainName, int domain, Integer id,
			AsyncCallback<Mod193Detail> callback) {
		AON.start();
		fsa.getMod193Detail(domainName, domain, id,
				new AsyncCallbackWrapper<Mod193Detail>(callback));
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
			AsyncCallback<ArrayList<Mod180>> callback) {
		AON.start();
		fsa.getMod180s(domainName, domainId,
				new AsyncCallbackWrapper<ArrayList<Mod180>>(callback));
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
			AsyncCallback<ArrayList<Mod184>> callback) {
		AON.start();
		fsa.getMod184s(domainName, domain,
				new AsyncCallbackWrapper<ArrayList<Mod184>>(callback));
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
		fsa.getMod184(domainName, domain, id,
				new AsyncCallbackWrapper<Mod184>(callback));
	}

	// ---------------------------------------------------------------MODELO 390
	@Override
	public void getMod390(String domainName, Integer domain, Integer id,
			AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.getMod390(domainName, domain, id,
				new AsyncCallbackWrapper<Mod390>(callback));
	}

	@Override
	public void getMod390s(String domainName, Integer domain,
			AsyncCallback<ArrayList<Mod390>> callback) {
		AON.start();
		fsa.getMod390s(domainName, domain,
				new AsyncCallbackWrapper<ArrayList<Mod390>>(callback));
	}

	@Override
	public void saveMod390(String domainName, Integer domain, Mod390 mod390,
			AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.saveMod390(domainName, domain, mod390,
				new AsyncCallbackWrapper<Mod390>(callback));
	}

	@Override
	public void deleteMod390(String domainName, Integer domain, Mod390 mod390,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod390(domainName, domain, mod390,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getMod390Details(String domainName, Integer domain, Mod390 mod390,
			AsyncCallback<ArrayList<Mod390Detail>> callback) {
		AON.start();
		fsa.getMod390Details(domainName,domain, mod390,
				new AsyncCallbackWrapper<ArrayList<Mod390Detail>>(callback));
	}

	@Override
	public void initializeMod390(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.initializeMod390(domainName, domain, year, 
				new AsyncCallbackWrapper<Mod390>(callback));
	}
	// ---------------------------------------------------------------MODELO 131

	@Override
	public void getMod131(String domainName, int domain, int id,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.getMod131(domainName, domain,id, 
						new AsyncCallbackWrapper<Mod131>(callback));
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
	public void saveMod131(String domainName, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.saveMod131(domainName, mod131, 
				new AsyncCallbackWrapper<Mod131>(callback));
	}

	@Override
	public void deleteMod131(String domainName, Mod131 mod131,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod131(domainName,mod131,  
				new AsyncCallbackWrapper<Void>(callback));
	}


	@Override
	public void initializeMod131(String domainName, int domain, Mod131 mod131,
			AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.initializeMod131(domainName, domain,mod131, 
				new AsyncCallbackWrapper<Mod131>(callback));
		
	}

	// ---------------------------------------------------------------MODELO 202

	@Override
	public void getMod202(String domainName, int domain, int id,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.getMod202(domainName, domain,id, 
						new AsyncCallbackWrapper<Mod202>(callback));
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
		fsa.saveMod202(domainName, mod202, 
				new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void initializeMod202(String domainName, int currentDomain,Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.initializeMod202(domainName, currentDomain,mod202, 
				new AsyncCallbackWrapper<Mod202>(callback));
		
	}

	@Override
	public void deleteMod202(String domainName, Mod202 mod202,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod202(domainName,mod202,  
				new AsyncCallbackWrapper<Void>(callback));
	}

	// ---------------------------------------------------------------MODELO 200

	@Override
	public void initializeMod200(String domainName, int domain, Mod200 mod200,
			AsyncCallback<Mod200> callback) {
		AON.start();
		fsa.initializeMod200(domainName,domain, mod200,  
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void getMod200ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod200> callback) {
		AON.start();
		fsa.getMod200ByYear(domainName,domain, year,  
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void calculateMod200(Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		fsa.calculateMod200(mod200,new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void deleteMod200(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod200(domainName,domain, id,  
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEAT(Mod200 mod200, AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEAT(mod200,  
				new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getMod200ById(String domainName, int domain, int id,
			AsyncCallback<Mod200> callback) {
		AON.start();
		fsa.getMod200ById(domainName,domain, id,  
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void saveMod200(String domainName, int domain, Mod200 mod200,
			AsyncCallback<Mod200> callback) {
		AON.start();
		fsa.saveMod200(domainName,domain, mod200,  
				new AsyncCallbackWrapper<Mod200>(callback));
		
	}

	@Override
	public void validateMod200(Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		fsa.validateMod200(mod200,  
				new AsyncCallbackWrapper<Mod200>(callback));
	}
	
	// --------------------------------------------------------------- NORMALIZED MEMORY
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


}
