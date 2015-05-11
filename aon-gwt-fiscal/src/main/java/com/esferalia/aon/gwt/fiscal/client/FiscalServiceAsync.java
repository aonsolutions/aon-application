package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;
import java.util.LinkedList;

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

public interface FiscalServiceAsync {
	// -------------------------------------------------------------- PARAMS
	void getFiscalParameters(String domainName,int domain,AsyncCallback<FiscalParameters> callback);

	// ------------------------------------------------------ FISCAL PANEL
	void getFiscalPanel(String currentDomainName,int currentDomain,int year,
			AsyncCallback<FiscalModelMatrix> asyncCallback);
	void getAllModels(String domainName, int domain, 
			AsyncCallback<LinkedList<IFiscalModel>> callback);
	void getAllModels(String domainName, int domain, int year,
			AsyncCallback<LinkedList<IFiscalModel>> callback);

	// ------------------------------------------------------ FISCAL ACTIVITIES
	void calculate(String domainName, FiscalActivity fa,AsyncCallback<FiscalActivity> callback);
	void getModuleEpigraphs(int year, AsyncCallback<ArrayList<Epigraph>> callback);
	void getFiscalActivities(String domainName, int domain,
			AsyncCallback<ArrayList<FiscalActivity>> callback);
	void getFiscalActivity(String domainName, int domain, int id,
			AsyncCallback<FiscalActivity> callback);
	void getFiscalActivityFor(String domainName, Epigraph epigraph,
			FiscalActivity fa, AsyncCallback<FiscalActivity> asyncCallback);
	void save(String domainName, FiscalActivity fa,
			AsyncCallback<FiscalActivity> callback);
	void delete(String domainName, FiscalActivity fa,
			AsyncCallback<Void> callback);
	
	// ------------------------------------------------------ FISCAL MODELS
	void getFiscalModels(String domainName, int domain,AsyncCallback<LinkedList<FiscalModel>> callback);
	void getFiscalModel(String domainName, int domain, int id,AsyncCallback<FiscalModel> callback);
	void save(String domainName, FiscalModel fm,AsyncCallback<FiscalModel> callback);
	void delete(String domainName, FiscalModel fm, AsyncCallback<Void> callback);

	// -------------------------------------------------------------- ACTIVITIES
	void getActivities(int activityGroup,
			AsyncCallback<ArrayList<Activity>> callback);

	// ---------------------------------------------------------------MODELO 190
	void deleteMod190(String domainName, int domain,Mod190 mod190, AsyncCallback<Void> callback);
	void saveMod190(String domainName, int domain,Mod190 mod190, AsyncCallback<Mod190> callback);
	void getMod190s(String domainName, int domain, AsyncCallback<ArrayList<Mod190>> callback);
	void getMod190(String domainName, int domain,Integer id, AsyncCallback<Mod190> callback);
	void getMod190Detail(String domainName, int domain,Integer id, AsyncCallback<Mod190Detail> callback);
	void initializeMod190(String domainName, Integer domain, Integer year,AsyncCallback<Mod190> callback);

	// ---------------------------------------------------------------MODELO 180
	void deleteMod180(String domainName, int domain, Mod180 mod180,AsyncCallback<Void> callback);
	void saveMod180(String domainName, int domain, Mod180 mod180, AsyncCallback<Mod180> callback);
	void getMod180s(String domainName, int domain, AsyncCallback<ArrayList<Mod180>> callback);
	void getMod180(String domainName, int domain, Integer id, AsyncCallback<Mod180> callback);
	void getMod180Detail(String domainName, int domain, Integer id, AsyncCallback<Mod180Detail> callback);
	void initializeMod180(String domainName, Integer domain, Integer year,AsyncCallback<Mod180> callback);

	// ---------------------------------------------------------------MODELO 184
	void deleteMod184(String domainName, int domain,Mod184 mod184, AsyncCallback<Void> callback);
	void saveMod184(String domainName, int domain,Mod184 mod184, AsyncCallback<Mod184> callback);
	void getMod184s(String domainName, int domain, AsyncCallback<ArrayList<Mod184>> callback);
	void getMod184(String domainName, int domain,Integer id, AsyncCallback<Mod184> callback);
	void initializeMod184(String domainName, Integer domain, Integer year,AsyncCallback<Mod184> callback);

	// ---------------------------------------------------------------MODELO 193
	void deleteMod193(String domainName, int domain,Mod193 mod193, AsyncCallback<Void> callback);
	void saveMod193(String domainName, int domain,Mod193 mod193, AsyncCallback<Mod193> callback);
	void getMod193s(String domainName, int domain, AsyncCallback<ArrayList<Mod193>> callback);
	void getMod193(String domainName, int domain,Integer id, AsyncCallback<Mod193> callback);
	void getMod193Detail(String domainName, int domain,Integer id, AsyncCallback<Mod193Detail> callback);
	void initializeMod193(String domainName, Integer domain, Integer year,AsyncCallback<Mod193> callback);

	// ---------------------------------------------------------------MODELO 390
	void getMod390(String domainName, Integer domain,Integer id, AsyncCallback<Mod390> callback);
	void getMod390s(String domainName, Integer domain, AsyncCallback<ArrayList<Mod390>> callback);
	void saveMod390(String domainName, Integer domain, Mod390 mod390, AsyncCallback<Mod390> callback);
	void deleteMod390(String domainName, Integer domain, Mod390 mod390, AsyncCallback<Void> callback);
	void getMod390Details(String domainName, Integer domain, Mod390 mod390,AsyncCallback<ArrayList<Mod390Detail>> callback);
	void initializeMod390(String domainName, Integer domain, Integer year, AsyncCallback<Mod390> callback);

	// ---------------------------------------------------------------MODELO 131
	void getMod131(String domainName, int domain, int id,AsyncCallback<Mod131> callback);
	void getMod131s(String domainName, int domain,AsyncCallback<LinkedList<Mod131>> callback);
	void calculateMod131(String domainName, Mod131 mod131,AsyncCallback<Mod131> callback);
	void saveMod131(String domainName, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);
	void deleteMod131(String currentDomainName, Mod131 mod131,AsyncCallback<Void> callback);
	void initializeMod131(String domainName, int domain, Mod131 mod131,AsyncCallback<Mod131> asyncCallback);

	// ---------------------------------------------------------------MODELO 202
	void getMod202(String domainName, int domain, int id,AsyncCallback<Mod202> callback);
	void getMod202s(String domainName, int domain,AsyncCallback<LinkedList<Mod202>> callback);
	void calculateMod202(String domainName, Mod202 mod202,AsyncCallback<Mod202> callback);
	void deleteMod202(String currentDomainName, Mod202 treeObject,AsyncCallback<Void> callback);
	void saveMod202(String domainName, Mod202 mod202,AsyncCallback<Mod202> asyncCallback);
	void initializeMod202(String domainName, int domain, Mod202 mod202,AsyncCallback<Mod202> asyncCallback);

	// ---------------------------------------------------------------MODELO 200
	void initializeMod200(String domainName, int domain, Mod200 mod200,AsyncCallback<Mod200> callback);
	void getMod200ByYear(String domainName, int domain, int year,AsyncCallback<Mod200> callback);
	void calculateMod200(Mod200 mod200, AsyncCallback<Mod200> callback);
	void deleteMod200(String domainName, int domain, int id,AsyncCallback<Void> callback);
	void dumpAEAT(Mod200 mod200, AsyncCallback<String> callback);
	void getMod200ById(String domainName, int domain, int id,AsyncCallback<Mod200> callback);
	void saveMod200(String domainName, int domain, Mod200 mod200,AsyncCallback<Mod200> callback);
	void validateMod200(Mod200 mod200, AsyncCallback<Mod200> callback);


}
