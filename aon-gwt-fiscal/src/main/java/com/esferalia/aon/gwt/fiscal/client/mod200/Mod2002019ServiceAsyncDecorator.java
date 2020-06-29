package com.esferalia.aon.gwt.fiscal.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002019ServiceAsyncDecorator implements Mod2002019ServiceAsync {

	private Mod2002019ServiceAsync fsa;

	public Mod2002019ServiceAsyncDecorator(Mod2002019ServiceAsync Mod2002019ServiceAsync) {
		this.fsa = Mod2002019ServiceAsync;
	}

	@Override
	public void createMod2002019(String domainName, int domain, int year,
			AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.createMod2002019(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002019>(callback));
	}

	@Override
	public void initializeNewMod2002019(String domainName, int domain,
			Mod2002019 mod200, AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.initializeNewMod2002019(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002019>(callback));
	}

	@Override
	public void initializeMod2002019(String domainName, int domain,
			Mod2002019 mod200, AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.initializeMod2002019(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002019>(callback));
	}

	@Override
	public void getMod2002019ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.getMod2002019ByYear(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002019>(callback));
	}

	@Override
	public void calculateMod2002019(Mod2002019 mod200,
			AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.calculateMod2002019(mod200, new AsyncCallbackWrapper<Mod2002019>(
				callback));
	}

	@Override
	public void deleteMod2002019(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002019(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002019(Mod2002019 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002019(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002019ById(String domainName, int domain, int id,
			AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.getMod2002019ById(domainName, domain, id,
				new AsyncCallbackWrapper<Mod2002019>(callback));
	}

	@Override
	public void saveMod2002019(String domainName, int domain,
			Mod2002019 mod200, AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.saveMod2002019(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002019>(callback));

	}

	@Override
	public void validateMod2002019(Mod2002019 mod200,
			AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.validateMod2002019(mod200, new AsyncCallbackWrapper<Mod2002019>(
				callback));
	}

	@Override
	public void importMod2002018(String domainName, int domain,
			Mod2002019 mod200, AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.importMod2002018(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002019>(callback));
	}

	@Override
	public void fillMod2002019AccountingData(Mod2002019 mod200,
			AsyncCallback<Mod2002019> callback) {
		AON.start();
		fsa.fillMod2002019AccountingData(mod200,
				new AsyncCallbackWrapper<Mod2002019>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(domainName,domain,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
}
