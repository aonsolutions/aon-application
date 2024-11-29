package com.esferalia.aon.gwt.mod200.client.mod200.e2018;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002018ServiceAsyncDecorator implements Mod2002018ServiceAsync {

	private Mod2002018ServiceAsync fsa;

	public Mod2002018ServiceAsyncDecorator(Mod2002018ServiceAsync Mod2002018ServiceAsync) {
		this.fsa = Mod2002018ServiceAsync;
	}

	@Override
	public void createMod2002018(String domainName, int domain, int year,
			AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.createMod2002018(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002018>(callback));
	}

	@Override
	public void initializeNewMod2002018(String domainName, int domain,
			Mod2002018 mod200, AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.initializeNewMod2002018(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002018>(callback));
	}

	@Override
	public void initializeMod2002018(String domainName, int domain,
			Mod2002018 mod200, AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.initializeMod2002018(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002018>(callback));
	}

	@Override
	public void getMod2002018ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.getMod2002018ByYear(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002018>(callback));
	}

	@Override
	public void calculateMod2002018(Mod2002018 mod200,
			AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.calculateMod2002018(mod200, new AsyncCallbackWrapper<Mod2002018>(
				callback));
	}

	@Override
	public void deleteMod2002018(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002018(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002018(Mod2002018 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002018(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002018ById(String domainName, int domain, int id,
			AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.getMod2002018ById(domainName, domain, id,
				new AsyncCallbackWrapper<Mod2002018>(callback));
	}

	@Override
	public void saveMod2002018(String domainName, int domain,
			Mod2002018 mod200, AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.saveMod2002018(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002018>(callback));

	}

	@Override
	public void validateMod2002018(Mod2002018 mod200,
			AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.validateMod2002018(mod200, new AsyncCallbackWrapper<Mod2002018>(
				callback));
	}

	@Override
	public void importMod2002017(String domainName, int domain,
			Mod2002018 mod200, AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.importMod2002017(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002018>(callback));
	}

	@Override
	public void fillMod2002018AccountingData(Mod2002018 mod200, String data,
			AsyncCallback<Mod2002018> callback) {
		AON.start();
		fsa.fillMod2002018AccountingData(mod200, data,
				new AsyncCallbackWrapper<Mod2002018>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(domainName,domain,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
}
