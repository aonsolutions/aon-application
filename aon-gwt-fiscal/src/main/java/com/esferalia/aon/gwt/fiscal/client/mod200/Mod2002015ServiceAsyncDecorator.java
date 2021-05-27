package com.esferalia.aon.gwt.fiscal.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002015ServiceAsyncDecorator implements Mod2002015ServiceAsync {

	private Mod2002015ServiceAsync fsa;

	public Mod2002015ServiceAsyncDecorator(Mod2002015ServiceAsync mod2002015ServiceAsync) {
		this.fsa = mod2002015ServiceAsync;
	}

	@Override
	public void createMod2002015(String domainName, int domain, int year,
			AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.createMod2002015(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002015>(callback));
	}

	@Override
	public void initializeNewMod2002015(String domainName, int domain,
			Mod2002015 mod200, AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.initializeNewMod2002015(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002015>(callback));
	}

	@Override
	public void initializeMod2002015(String domainName, int domain,
			Mod2002015 mod200, AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.initializeMod2002015(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002015>(callback));
	}

	@Override
	public void getMod2002015ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.getMod2002015ByYear(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002015>(callback));
	}

	@Override
	public void calculateMod2002015(Mod2002015 mod200,
			AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.calculateMod2002015(mod200, new AsyncCallbackWrapper<Mod2002015>(
				callback));
	}

	@Override
	public void deleteMod2002015(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002015(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002015(Mod2002015 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002015(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002015ById(String domainName, int domain, int id,
			AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.getMod2002015ById(domainName, domain, id,
				new AsyncCallbackWrapper<Mod2002015>(callback));
	}

	@Override
	public void saveMod2002015(String domainName, int domain,
			Mod2002015 mod200, AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.saveMod2002015(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002015>(callback));

	}

	@Override
	public void validateMod2002015(Mod2002015 mod200,
			AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.validateMod2002015(mod200, new AsyncCallbackWrapper<Mod2002015>(
				callback));
	}

	@Override
	public void importMod2002014(String domainName, int domain,
			Mod2002015 mod200, AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.importMod2002014(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002015>(callback));
	}

	@Override
	public void fillMod2002015AccountingData(Mod2002015 mod200,
			AsyncCallback<Mod2002015> callback) {
		AON.start();
		fsa.fillMod2002015AccountingData(mod200,
				new AsyncCallbackWrapper<Mod2002015>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(domainName,domain,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}

}
