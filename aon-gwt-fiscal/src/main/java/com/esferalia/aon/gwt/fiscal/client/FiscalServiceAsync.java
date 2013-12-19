package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.fiscal.shared.Enterprise;
import com.esferalia.aon.gwt.fiscal.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.Mod180;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Receiver;
import com.esferalia.aon.gwt.fiscal.shared.Mod190;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Receiver;
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

	void generateMod190File(Integer id, int year, int administration,
			AsyncCallback<String> asyncCallback);

	// ---------------------------------------------------------------MODELO 180
	void deleteMod180(Mod180 mod180, AsyncCallback<Void> callback);

	void saveMod180(Mod180 mod180, AsyncCallback<Mod180> callback);

	void saveMod180(Mod180 mod180, ArrayList<Mod180Receiver> perceptors,
			AsyncCallback<Mod180> callback);

	void getMod180s(int domain, AsyncCallback<ArrayList<Mod180>> callback);

	void getMod180(Integer id, AsyncCallback<Mod180> callback);

	void getMod180DetailByMod180(int domain, int offset, int limit,
			AsyncCallback<ArrayList<Mod180Detail>> callback);

	void getMod180Detail(Integer id, AsyncCallback<Mod180Receiver> callback);

	void generateMod180File(Integer id, int year, int administration,
			AsyncCallback<String> asyncCallback);
}

