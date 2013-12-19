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
	public void getMod190Detail(Integer id, AsyncCallback<Mod190Receiver> callback) {
		AON.start();
		fiscalServiceAsync.getMod190Detail(id,
				new AsyncCallbackWrapper<Mod190Receiver>(callback));
	}
	

	@Override
	public void generateMod190File(Integer id, int year, int administration, AsyncCallback<String> callback) {
		AON.start();
		fiscalServiceAsync.generateMod190File(id,year,administration,
				new AsyncCallbackWrapper<String>(callback));
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
	public void getMod180Detail(Integer id, AsyncCallback<Mod180Receiver> callback) {
		AON.start();
		fiscalServiceAsync.getMod180Detail(id,
				new AsyncCallbackWrapper<Mod180Receiver>(callback));
	}
	

	@Override
	public void generateMod180File(Integer id, int year, int administration, AsyncCallback<String> callback) {
		AON.start();
		fiscalServiceAsync.generateMod180File(id,year,administration,
				new AsyncCallbackWrapper<String>(callback));
	}
}
