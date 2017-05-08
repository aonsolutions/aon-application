package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002013ServiceAsyncDecorator implements Mod2002013ServiceAsync {

	private Mod2002013ServiceAsync fsa;

	public Mod2002013ServiceAsyncDecorator(Mod2002013ServiceAsync mod2002013ServiceAsync) {
		this.fsa = mod2002013ServiceAsync;
	}

	@Override
	public void createMod2002013(String domainName, int domain, int year,
			AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.createMod2002013(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002013>(callback));
	}

	@Override
	public void initializeNewMod2002013(String domainName, int domain,
			Mod2002013 mod200, AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.initializeNewMod2002013(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002013>(callback));
	}

	@Override
	public void initializeMod2002013(String domainName, int domain,
			Mod2002013 mod200, AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.initializeMod2002013(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002013>(callback));
	}

	@Override
	public void getMod2002013ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.getMod2002013ByYear(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002013>(callback));
	}

	@Override
	public void calculateMod2002013(Mod2002013 mod200,
			AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.calculateMod2002013(mod200, new AsyncCallbackWrapper<Mod2002013>(
				callback));
	}

	@Override
	public void deleteMod2002013(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002013(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002013(Mod2002013 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002013(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002013ById(String domainName, int domain, int id,
			AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.getMod2002013ById(domainName, domain, id,
				new AsyncCallbackWrapper<Mod2002013>(callback));
	}

	@Override
	public void saveMod2002013(String domainName, int domain,
			Mod2002013 mod200, AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.saveMod2002013(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002013>(callback));

	}

	@Override
	public void validateMod2002013(Mod2002013 mod200,
			AsyncCallback<Mod2002013> callback) {
		AON.start();
		fsa.validateMod2002013(mod200, new AsyncCallbackWrapper<Mod2002013>(
				callback));
	}

}
