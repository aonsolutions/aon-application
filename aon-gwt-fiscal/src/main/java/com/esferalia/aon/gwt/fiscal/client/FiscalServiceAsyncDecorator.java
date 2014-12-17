package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.Enterprise;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.occam.api.model.Mod180;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.esferalia.aon.occam.api.model.Mod190;
import com.esferalia.aon.occam.api.model.Mod190Detail;
import com.esferalia.aon.occam.api.model.Mod390;
import com.esferalia.aon.occam.api.model.Mod390.Activity;
import com.esferalia.aon.occam.api.model.Mod390.Mod303Results;
import com.esferalia.aon.occam.api.model.Mod390.Mod390Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FiscalServiceAsyncDecorator implements FiscalServiceAsync {

	private FiscalServiceAsync fiscalServiceAsync;

	public FiscalServiceAsyncDecorator(FiscalServiceAsync mod190ServiceAsync) {
		this.fiscalServiceAsync = mod190ServiceAsync;
	}

	// ------------------------------------------------------- FISCAL PARAMETERS
	@Override
	public void getFiscalParameters(int domain,
			AsyncCallback<FiscalParameters> callback) {
		AON.start();
		fiscalServiceAsync.getFiscalParameters(domain,
				new AsyncCallbackWrapper<FiscalParameters>(callback));
	}

	// -------------------------------------------------------------- ENTERPRISE
	@Override
	public void getEnterprises(int domain, String query,
			AsyncCallback<ArrayList<Enterprise>> callback) {
		AON.start();
		fiscalServiceAsync.getEnterprises(domain, query,
				new AsyncCallbackWrapper<ArrayList<Enterprise>>(callback));
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
	public void getMod303Results(String domainName, Integer domain, int year,
			AsyncCallback<Mod303Results> callback) {
		AON.start();
		fiscalServiceAsync.getMod303Results(domainName,domain, year,
				new AsyncCallbackWrapper<Mod303Results>(callback));
	}

	@Override
	public void getMod311Results(int domain, int year,
			AsyncCallback<ArrayList<Mod311Results>> callback) {
		AON.start();
		fiscalServiceAsync.getMod311Results(domain, year,
				new AsyncCallbackWrapper<ArrayList<Mod311Results>>(callback));
	}

}
