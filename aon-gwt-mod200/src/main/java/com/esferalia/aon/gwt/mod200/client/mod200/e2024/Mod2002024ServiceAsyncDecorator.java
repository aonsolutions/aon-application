package com.esferalia.aon.gwt.mod200.client.mod200.e2024;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002024ServiceAsyncDecorator implements Mod2002024ServiceAsync {

	private Mod2002024ServiceAsync fsa;

	public Mod2002024ServiceAsyncDecorator(Mod2002024ServiceAsync mod2002024ServiceAsync) {
		this.fsa = mod2002024ServiceAsync;
	}

	@Override
	public void createMod2002024(Occam occam, int year,
			AsyncCallback<Mod2002024> callback) {
		AON.start();
		fsa.createMod2002024(occam, year,
				new AsyncCallbackWrapper<Mod2002024>(callback));
	}

	@Override
	public void initializeMod2002024(Occam occam,
			Mod2002024 mod200, AsyncCallback<Mod2002024> callback) {
		AON.start();
		fsa.initializeMod2002024(occam, mod200,
				new AsyncCallbackWrapper<Mod2002024>(callback));
	}

	@Override
	public void calculateMod2002024(Mod2002024 mod200,
			AsyncCallback<Mod2002024> callback) {
		AON.start();
		fsa.calculateMod2002024(mod200, new AsyncCallbackWrapper<Mod2002024>(
				callback));
	}

	@Override
	public void deleteMod2002024(Occam occam, Mod2002024 mod200,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002024(occam, mod200,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getMod2002024ById(Occam occam, int id,
			AsyncCallback<Mod2002024> callback) {
		AON.start();
		fsa.getMod2002024ById(occam, id, 
				new AsyncCallbackWrapper<Mod2002024>(callback));
	}

	@Override
	public void saveMod2002024(Occam occam, 
			Mod2002024 mod200, AsyncCallback<Mod2002024> callback) {
		AON.start();
		fsa.saveMod2002024(occam, mod200, 
				new AsyncCallbackWrapper<Mod2002024>(callback));

	}

	@Override
	public void fillMod2002024AccountingData(Occam occam, Mod2002024 mod200, String data,
			AsyncCallback<Mod2002024> callback) {
		AON.start();
		fsa.fillMod2002024AccountingData(occam, mod200, data,
				new AsyncCallbackWrapper<Mod2002024>(callback));
	}

	@Override
	public void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(occam,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
}
