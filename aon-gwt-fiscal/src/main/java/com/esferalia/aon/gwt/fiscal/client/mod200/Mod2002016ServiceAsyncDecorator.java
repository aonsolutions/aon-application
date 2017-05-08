package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002016ServiceAsyncDecorator implements Mod2002016ServiceAsync {

	private Mod2002016ServiceAsync fsa;

	public Mod2002016ServiceAsyncDecorator(Mod2002016ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void createMod2002016(String domainName, int domain, int year,
			AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.createMod2002016(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002016>(callback));
	}

	@Override
	public void initializeNewMod2002016(String domainName, int domain,
			Mod2002016 mod200, AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.initializeNewMod2002016(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002016>(callback));
	}

	@Override
	public void initializeMod2002016(String domainName, int domain,
			Mod2002016 mod200, AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.initializeMod2002016(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002016>(callback));
	}

	@Override
	public void getMod2002016ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.getMod2002016ByYear(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002016>(callback));
	}

	@Override
	public void calculateMod2002016(Mod2002016 mod200,
			AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.calculateMod2002016(mod200, new AsyncCallbackWrapper<Mod2002016>(
				callback));
	}

	@Override
	public void deleteMod2002016(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002016(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002016(Mod2002016 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002016(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002016ById(String domainName, int domain, int id,
			AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.getMod2002016ById(domainName, domain, id,
				new AsyncCallbackWrapper<Mod2002016>(callback));
	}

	@Override
	public void saveMod2002016(String domainName, int domain,
			Mod2002016 mod200, AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.saveMod2002016(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002016>(callback));

	}

	@Override
	public void validateMod2002016(Mod2002016 mod200,
			AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.validateMod2002016(mod200, new AsyncCallbackWrapper<Mod2002016>(
				callback));
	}

	@Override
	public void importMod2002015(String domainName, int domain,
			Mod2002016 mod200, AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.importMod2002015(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002016>(callback));
	}

	@Override
	public void fillMod2002016AccountingData(Mod2002016 mod200,
			AsyncCallback<Mod2002016> callback) {
		AON.start();
		fsa.fillMod2002016AccountingData(mod200,
				new AsyncCallbackWrapper<Mod2002016>(callback));
	}
	
}
