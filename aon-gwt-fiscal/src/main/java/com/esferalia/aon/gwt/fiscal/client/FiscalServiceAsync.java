package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.Activity;
import com.esferalia.aon.gwt.fiscal.shared.Enterprise;
import com.esferalia.aon.gwt.fiscal.shared.Mod190;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Receiver;
import com.esferalia.aon.gwt.fiscal.shared.Mod303Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.shared.Mod390Detail;
import com.esferalia.aon.occam.api.model.Mod180;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EmployeesService</code>.
 */
public interface FiscalServiceAsync {

	// ------------------------------------------------------- FISCAL PARAMETERS
	void getFiscalParameters(int domain,
			AsyncCallback<FiscalParameters> callback);

	// -------------------------------------------------------------- ENTERPRISE
	void getEnterprises(int domain, String query,
			AsyncCallback<ArrayList<Enterprise>> callback);

	// -------------------------------------------------------------- ACTIVITIES
	void getActivities(int activityGroup,
			AsyncCallback<ArrayList<Activity>> callback);

	// ---------------------------------------------------------------MODELO 190
	void deleteMod190(Mod190 mod190, AsyncCallback<Void> callback);

	void saveMod190(Mod190 mod190, AsyncCallback<Mod190> callback);

	void saveMod190(Mod190 mod190, ArrayList<Mod190Receiver> perceptors,
			AsyncCallback<Mod190> callback);

	void getMod190s(int domain, AsyncCallback<ArrayList<Mod190>> callback);

	void getMod190(Integer id, AsyncCallback<Mod190> callback);

	void getMod190DetailByMod190(int domain, int offset, int limit,
			AsyncCallback<ArrayList<Mod190Detail>> callback);

	void getMod190Detail(Integer id, AsyncCallback<Mod190Receiver> callback);

	// ---------------------------------------------------------------MODELO 180
	void deleteMod180(String domainName, int domain, Mod180 mod180,
			AsyncCallback<Void> callback);

	void saveMod180(String domainName, int domain, Mod180 mod180,
			AsyncCallback<Mod180> callback);

	void getMod180s(String domainName, int domain,
			AsyncCallback<ArrayList<Mod180>> callback);

	void getMod180(String domainName, int domain, Integer id,
			AsyncCallback<Mod180> callback);

	void getMod180Detail(String domainName, int domain, Integer id,
			AsyncCallback<Mod180Detail> callback);

	// ---------------------------------------------------------------MODELO 390
	void getMod390Details(int domain, Integer year,
			AsyncCallback<ArrayList<Mod390Detail>> callback);

	void getMod303Results(int domain, int year,
			AsyncCallback<Mod303Results> asyncCallback);

	void getMod311Results(int domain, int year,
			AsyncCallback<ArrayList<Mod311Results>> asyncCallback);

	void getMod390(Integer id, AsyncCallback<Mod390> callback);

	void getMod390s(int domain, AsyncCallback<ArrayList<Mod390>> callback);

	void saveMod390(Mod390 mod390, AsyncCallback<Mod390> callback);

	void deleteMod390(Mod390 mod390, AsyncCallback<Void> callback);
	

}
