package com.esferalia.aon.gwt.fiscal.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod200ServiceAsyncDecorator implements Mod200ServiceAsync {

	private Mod200ServiceAsync mod200ServiceAsync;

	public Mod200ServiceAsyncDecorator(Mod200ServiceAsync mod200ServiceAsync) {
		this.mod200ServiceAsync = mod200ServiceAsync;
	}

	@Override
	public void getMod200(String domainName,int domain, int year, AsyncCallback<Mod200> callback)  {
		AON.start();
		mod200ServiceAsync.getMod200(domainName,domain, year,
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void calculate(Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.calculate(mod200,
				new AsyncCallbackWrapper<Mod200>(callback));
		
	}

	@Override
	public void save(String domainName,int domain, Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.save(domainName,domain,mod200, 
				new AsyncCallbackWrapper<Mod200>(callback));
		
	}

	@Override
	public void initialize(String domainName,int domain, Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.initialize(domainName,domain,mod200,
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	public void validate(Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.validate(mod200,
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void delete(String domainName,int domain, Mod200 mod200, AsyncCallback<Mod200> callback) {
		AON.start();
		mod200ServiceAsync.delete(domainName,domain,mod200,
				new AsyncCallbackWrapper<Mod200>(callback));
	}

	@Override
	public void dumpAEAT(Mod200 mod200, AsyncCallback<String> callback) {
		AON.start();
		mod200ServiceAsync.dumpAEAT(mod200,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getCompanyBanks(String domainName,int domain,int enterprise,AsyncCallback<ArrayList<CompanyBank>> callback) {
		AON.start();
		mod200ServiceAsync.getCompanyBanks(domainName,domain, enterprise,
				new AsyncCallbackWrapper<ArrayList<CompanyBank>>(callback));
	}

}
