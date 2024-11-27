package com.esferalia.aon.gwt.mod200.client.mod200.e2014;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.mod200.api.model.mod200_2014.Mod2002014;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002014ServiceAsyncDecorator implements Mod2002014ServiceAsync {

	private Mod2002014ServiceAsync fsa;

	public Mod2002014ServiceAsyncDecorator(Mod2002014ServiceAsync mod2002014ServiceAsync) {
		this.fsa = mod2002014ServiceAsync;
	}

	@Override
	public void createMod2002014(String domainName, int domain, int year,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.createMod2002014(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void initializeNewMod2002014(String domainName, int domain,
			Mod2002014 mod200, AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.initializeNewMod2002014(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void initializeMod2002014(String domainName, int domain,
			Mod2002014 mod200, String data, AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.initializeMod2002014(domainName, domain, mod200, data,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void getMod2002014ByYear(String domainName, int domain, int year,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.getMod2002014ByYear(domainName, domain, year,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void calculateMod2002014(Mod2002014 mod200,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.calculateMod2002014(mod200, new AsyncCallbackWrapper<Mod2002014>(
				callback));
	}

	@Override
	public void deleteMod2002014(String domainName, int domain, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002014(domainName, domain, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void dumpAEATMod2002014(Mod2002014 mod200,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.dumpAEATMod2002014(mod200, new AsyncCallbackWrapper<String>(
				callback));
	}

	@Override
	public void getMod2002014ById(String domainName, int domain, int id,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.getMod2002014ById(domainName, domain, id,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void saveMod2002014(String domainName, int domain,
			Mod2002014 mod200, AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.saveMod2002014(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002014>(callback));

	}

	@Override
	public void validateMod2002014(Mod2002014 mod200,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.validateMod2002014(mod200, new AsyncCallbackWrapper<Mod2002014>(
				callback));
	}

	@Override
	public void importMod2002013(String domainName, int domain,
			Mod2002014 mod200, AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.importMod2002013(domainName, domain, mod200,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void fillMod2002014AccountingData(Mod2002014 mod200, String data,
			AsyncCallback<Mod2002014> callback) {
		AON.start();
		fsa.fillMod2002014AccountingData(mod200, data,
				new AsyncCallbackWrapper<Mod2002014>(callback));
	}

	@Override
	public void getCompanyBanks(String domainName, int domain, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
		
	}

}
