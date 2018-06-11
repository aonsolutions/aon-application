package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002017ServiceAsyncDecorator implements Mod2002017ServiceAsync {

	private Mod2002017ServiceAsync fsa;

	public Mod2002017ServiceAsyncDecorator(Mod2002017ServiceAsync mod2002017ServiceAsync) {
		this.fsa = mod2002017ServiceAsync;
	}

	@Override
	public void createMod2002017(String domainName, int domain, int year,
			AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.createMod2002017(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002017>(callback));
	}

	@Override
	public void initializeNewMod2002017(String domainName, int domain,
			Mod2002017 mod200, AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.initializeNewMod2002017(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002017>(callback));
	}

	@Override
	public void initializeMod2002017(String domainName, int domain,
			Mod2002017 mod200, AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.initializeMod2002017(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002017>(callback));
	}

	@Override
	public void getMod2002017ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.getMod2002017ByYear(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002017>(callback));
	}

	@Override
	public void calculateMod2002017(Mod2002017 mod200,
			AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.calculateMod2002017(mod200, new AsyncCallbackWrapper<Mod2002017>(
				callback));
	}

	@Override
	public void deleteMod2002017(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002017(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002017(Mod2002017 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002017(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002017ById(String domainName, int domain, int id,
			AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.getMod2002017ById(domainName, domain, id,
				new AsyncCallbackWrapper<Mod2002017>(callback));
	}

	@Override
	public void saveMod2002017(String domainName, int domain,
			Mod2002017 mod200, AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.saveMod2002017(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002017>(callback));

	}

	@Override
	public void validateMod2002017(Mod2002017 mod200,
			AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.validateMod2002017(mod200, new AsyncCallbackWrapper<Mod2002017>(
				callback));
	}

	@Override
	public void importMod2002016(String domainName, int domain,
			Mod2002017 mod200, AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.importMod2002016(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002017>(callback));
	}

	@Override
	public void fillMod2002017AccountingData(Mod2002017 mod200,
			AsyncCallback<Mod2002017> callback) {
		AON.start();
		fsa.fillMod2002017AccountingData(mod200,
				new AsyncCallbackWrapper<Mod2002017>(callback));
	}

}
