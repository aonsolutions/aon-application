package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalServiceAsync {
	// -------------------------------------------------------------- PARAMS
	void getFiscalParameters(String domainName,int domain,AsyncCallback<FiscalParameters> callback);

	// ------------------------------------------------------ FISCAL PANEL
	void getFiscalPanel(String currentDomainName,int currentDomain,int y,
			AsyncCallback<FiscalModelMatrix> asyncCallback);
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

}
