package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002025ServiceAsyncDecorator implements Mod2002025ServiceAsync {

	private Mod2002025ServiceAsync fsa;

	public Mod2002025ServiceAsyncDecorator(Mod2002025ServiceAsync mod2002025ServiceAsync) {
		this.fsa = mod2002025ServiceAsync;
	}

	@Override
	public void createMod2002025(Occam occam, int year,
			AsyncCallback<Mod2002025> callback) {
		AON.start();
		fsa.createMod2002025(occam, year,
				new AsyncCallbackWrapper<Mod2002025>(callback));
	}

	@Override
	public void initializeMod2002025(Occam occam,
			Mod2002025 mod200, AsyncCallback<Mod2002025> callback) {
		AON.start();
		fsa.initializeMod2002025(occam, mod200,
				new AsyncCallbackWrapper<Mod2002025>(callback));
	}

	@Override
	public void calculateMod2002025(Mod2002025 mod200,
			AsyncCallback<Mod2002025> callback) {
		AON.start();
		fsa.calculateMod2002025(mod200, new AsyncCallbackWrapper<Mod2002025>(
				callback));
	}

	@Override
	public void deleteMod2002025(Occam occam, Mod2002025 mod200,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002025(occam, mod200,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getMod2002025ById(Occam occam, int id,
			AsyncCallback<Mod2002025> callback) {
		AON.start();
		fsa.getMod2002025ById(occam, id, 
				new AsyncCallbackWrapper<Mod2002025>(callback));
	}

	@Override
	public void saveMod2002025(Occam occam, 
			Mod2002025 mod200, AsyncCallback<Mod2002025> callback) {
		AON.start();
		fsa.saveMod2002025(occam, mod200, 
				new AsyncCallbackWrapper<Mod2002025>(callback));

	}

	@Override
	public void fillMod2002025AccountingData(Occam occam, Mod2002025 mod200, String data,
			AsyncCallback<Mod2002025> callback) {
		AON.start();
		fsa.fillMod2002025AccountingData(occam, mod200, data,
				new AsyncCallbackWrapper<Mod2002025>(callback));
	}

	@Override
	public void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(occam,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
}
