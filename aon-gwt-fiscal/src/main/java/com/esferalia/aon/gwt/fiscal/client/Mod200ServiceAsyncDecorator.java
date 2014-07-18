package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.shared.CompanyBank;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod200ServiceAsyncDecorator implements Mod200ServiceAsync {

	private Mod200ServiceAsync mod200ServiceAsync;

	public Mod200ServiceAsyncDecorator(Mod200ServiceAsync mod200ServiceAsync) {
		this.mod200ServiceAsync = mod200ServiceAsync;
	}

	@Override
	public void getMod200(int domain, int year, AsyncCallback<Mod200> callback)  {
		AON.start();
		mod200ServiceAsync.getMod200(domain, year,
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void calculate(Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.calculate(mod200,
				new AsyncCallbackWrapper<Mod200>(callback));
		
	}

	@Override
	public void save(Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.save(mod200, 
				new AsyncCallbackWrapper<Mod200>(callback));
		
	}

	@Override
	public void initialize(Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.initialize(mod200,
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	public void validate(Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.validate(mod200,
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void delete(Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.delete(mod200,
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void dumpAEAT(Mod200 mod200, AsyncCallback<String> callback) {
		AON.start();
		mod200ServiceAsync.dumpAEAT(mod200,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getCompanyBanks(int enterprise,AsyncCallback<ArrayList<CompanyBank>> callback) {
		AON.start();
		mod200ServiceAsync.getCompanyBanks(enterprise,
				new AsyncCallbackWrapper<ArrayList<CompanyBank>>(callback));
	}

}
