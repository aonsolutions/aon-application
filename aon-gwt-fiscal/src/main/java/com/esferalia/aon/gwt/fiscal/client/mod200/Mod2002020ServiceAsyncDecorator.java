package com.esferalia.aon.gwt.fiscal.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002020ServiceAsyncDecorator implements Mod2002020ServiceAsync {

	private Mod2002020ServiceAsync fsa;

	public Mod2002020ServiceAsyncDecorator(Mod2002020ServiceAsync Mod2002020ServiceAsync) {
		this.fsa = Mod2002020ServiceAsync;
	}

	@Override
	public void createMod2002020(String domainName, int domain, String user, int year,
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.createMod2002020(domainName, domain, user, year,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void initializeNewMod2002020(String domainName, int domain, String user,
			Mod2002020 mod200, AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.initializeNewMod2002020(domainName, domain, user, mod200,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void initializeMod2002020(String domainName, int domain, String user,
			Mod2002020 mod200, AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.initializeMod2002020(domainName, domain, user, mod200,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void getMod2002020ByYear(String domainName, int domain, String user, int year,
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.getMod2002020ByYear(domainName, domain, user, year, 
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void calculateMod2002020(Mod2002020 mod200,
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.calculateMod2002020(mod200, new AsyncCallbackWrapper<Mod2002020>(
				callback));
	}

	@Override
	public void deleteMod2002020(String domainName, int domain, String user, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002020(domainName, domain, user, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002020(Mod2002020 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002020(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002020ById(String domainName, int domain, String user, int id,
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.getMod2002020ById(domainName, domain, user, id, 
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void saveMod2002020(String domainName, int domain, String user, 
			Mod2002020 mod200, AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.saveMod2002020(domainName, domain, user, mod200, 
				new AsyncCallbackWrapper<Mod2002020>(callback));

	}

	@Override
	public void validateMod2002020(Mod2002020 mod200,
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.validateMod2002020(mod200, new AsyncCallbackWrapper<Mod2002020>(
				callback));
	}

	@Override
	public void importMod2002019(String domainName, int domain, String user,
			Mod2002020 mod200, AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.importMod2002019(domainName, domain, user, mod200,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void fillMod2002020AccountingData(Mod2002020 mod200,
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.fillMod2002020AccountingData(mod200,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, String user, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(domainName,domain, user,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
}
