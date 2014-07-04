package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.shared.FiscalParameters;
import com.esferalia.aon.gwt.fiscal.shared.Activity;
import com.esferalia.aon.gwt.fiscal.shared.Enterprise;
import com.esferalia.aon.gwt.fiscal.shared.Mod180;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Receiver;
import com.esferalia.aon.gwt.fiscal.shared.Mod190;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod190Receiver;
import com.esferalia.aon.gwt.fiscal.shared.Mod303Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.shared.Mod390Detail;
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
	public void deleteMod190(Mod190 mod190, AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.deleteMod190(mod190, new AsyncCallbackWrapper<Void>(
				callback));
	}

	@Override
	public void saveMod190(Mod190 mod190, AsyncCallback<Mod190> callback) {
		AON.start();
		fiscalServiceAsync.saveMod190(mod190, new AsyncCallbackWrapper<Mod190>(
				callback));
	}

	@Override
	public void saveMod190(Mod190 mod190, ArrayList<Mod190Receiver> perceptors,
			AsyncCallback<Mod190> callback) {
		AON.start();
		fiscalServiceAsync.saveMod190(mod190, perceptors,
				new AsyncCallbackWrapper<Mod190>(callback));
	}

	@Override
	public void getMod190s(int domain, AsyncCallback<ArrayList<Mod190>> callback) {
		AON.start();
		fiscalServiceAsync.getMod190s(domain,
				new AsyncCallbackWrapper<ArrayList<Mod190>>(callback));
	}

	@Override
	public void getMod190(Integer id, AsyncCallback<Mod190> callback) {
		AON.start();
		fiscalServiceAsync.getMod190(id, new AsyncCallbackWrapper<Mod190>(
				callback));
	}

	@Override
	public void getMod190DetailByMod190(int mod190, int offset, int limit,
			AsyncCallback<ArrayList<Mod190Detail>> callback) {
		AON.start();
		fiscalServiceAsync.getMod190DetailByMod190(mod190, offset, limit,
				new AsyncCallbackWrapper<ArrayList<Mod190Detail>>(callback));
	}

	@Override
	public void getMod190Detail(Integer id,
			AsyncCallback<Mod190Receiver> callback) {
		AON.start();
		fiscalServiceAsync.getMod190Detail(id,
				new AsyncCallbackWrapper<Mod190Receiver>(callback));
	}

	// ---------------------------------------------------------------MODELO 180
	@Override
	public void deleteMod180(Mod180 mod180, AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.deleteMod180(mod180, new AsyncCallbackWrapper<Void>(
				callback));
	}

	@Override
	public void saveMod180(Mod180 mod180, AsyncCallback<Mod180> callback) {
		AON.start();
		fiscalServiceAsync.saveMod180(mod180, new AsyncCallbackWrapper<Mod180>(
				callback));
	}

	@Override
	public void saveMod180(Mod180 mod180, ArrayList<Mod180Receiver> perceptors,
			AsyncCallback<Mod180> callback) {
		AON.start();
		fiscalServiceAsync.saveMod180(mod180, perceptors,
				new AsyncCallbackWrapper<Mod180>(callback));
	}

	@Override
	public void getMod180s(int domain, AsyncCallback<ArrayList<Mod180>> callback) {
		AON.start();
		fiscalServiceAsync.getMod180s(domain,
				new AsyncCallbackWrapper<ArrayList<Mod180>>(callback));
	}

	@Override
	public void getMod180(Integer id, AsyncCallback<Mod180> callback) {
		AON.start();
		fiscalServiceAsync.getMod180(id, new AsyncCallbackWrapper<Mod180>(
				callback));
	}

	@Override
	public void getMod180DetailByMod180(int mod180, int offset, int limit,
			AsyncCallback<ArrayList<Mod180Detail>> callback) {
		AON.start();
		fiscalServiceAsync.getMod180DetailByMod180(mod180, offset, limit,
				new AsyncCallbackWrapper<ArrayList<Mod180Detail>>(callback));
	}

	@Override
	public void getMod180Detail(Integer id,
			AsyncCallback<Mod180Receiver> callback) {
		AON.start();
		fiscalServiceAsync.getMod180Detail(id,
				new AsyncCallbackWrapper<Mod180Receiver>(callback));
	}

	// ---------------------------------------------------------------MODELO 390
	@Override
	public void getMod390Details(int domain, Integer year,
			AsyncCallback<ArrayList<Mod390Detail>> callback) {
		AON.start();
		fiscalServiceAsync.getMod390Details(domain, year,
				new AsyncCallbackWrapper<ArrayList<Mod390Detail>>(callback));
	}
	
	@Override
	public void getMod303Results(int domain, int year,
			AsyncCallback<Mod303Results> callback) {
		AON.start();
		fiscalServiceAsync.getMod303Results(domain, year,
				new AsyncCallbackWrapper<Mod303Results>(callback));
	}

	@Override
	public void getMod311Results(int domain, int year,
			AsyncCallback<ArrayList<Mod311Results>> callback) {
		AON.start();
		fiscalServiceAsync.getMod311Results(domain, year,
				new AsyncCallbackWrapper<ArrayList<Mod311Results>>(callback));
	}

	@Override
	public void getMod390(Integer id, AsyncCallback<Mod390> callback) {
		AON.start();
		fiscalServiceAsync.getMod390(id, new AsyncCallbackWrapper<Mod390>(
				callback));
	}

	@Override
	public void getMod390s(int domain, AsyncCallback<ArrayList<Mod390>> callback) {
		AON.start();
		fiscalServiceAsync.getMod390s(domain,
				new AsyncCallbackWrapper<ArrayList<Mod390>>(callback));
	}

	@Override
	public void saveMod390(Mod390 mod390, AsyncCallback<Mod390> callback) {
		AON.start();
		fiscalServiceAsync.saveMod390(mod390, new AsyncCallbackWrapper<Mod390>(
				callback));
	}

	@Override
	public void deleteMod390(Mod390 mod390, AsyncCallback<Void> callback) {
		AON.start();
		fiscalServiceAsync.deleteMod390(mod390, new AsyncCallbackWrapper<Void>(
				callback));
	}

}
