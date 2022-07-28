package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002021ServiceAsyncDecorator implements Mod2002021ServiceAsync {

	private Mod2002021ServiceAsync fsa;

	public Mod2002021ServiceAsyncDecorator(Mod2002021ServiceAsync Mod2002021ServiceAsync) {
		this.fsa = Mod2002021ServiceAsync;
	}

	@Override
	public void createMod2002021(Occam occam, int year,
			AsyncCallback<Mod2002021> callback) {
		AON.start();
		fsa.createMod2002021(occam, year,
				new AsyncCallbackWrapper<Mod2002021>(callback));
	}

	@Override
	public void initializeMod2002021(Occam occam,
			Mod2002021 mod200, AsyncCallback<Mod2002021> callback) {
		AON.start();
		fsa.initializeMod2002021(occam, mod200,
				new AsyncCallbackWrapper<Mod2002021>(callback));
	}

	@Override
	public void calculateMod2002021(Mod2002021 mod200,
			AsyncCallback<Mod2002021> callback) {
		AON.start();
		fsa.calculateMod2002021(mod200, new AsyncCallbackWrapper<Mod2002021>(
				callback));
	}

	@Override
	public void deleteMod2002021(Occam occam, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002021(occam, id,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getMod2002021ById(Occam occam, int id,
			AsyncCallback<Mod2002021> callback) {
		AON.start();
		fsa.getMod2002021ById(occam, id, 
				new AsyncCallbackWrapper<Mod2002021>(callback));
	}

	@Override
	public void saveMod2002021(Occam occam, 
			Mod2002021 mod200, AsyncCallback<Mod2002021> callback) {
		AON.start();
		fsa.saveMod2002021(occam, mod200, 
				new AsyncCallbackWrapper<Mod2002021>(callback));

	}

	@Override
	public void fillMod2002021AccountingData(Occam occam, Mod2002021 mod200, String data,
			AsyncCallback<Mod2002021> callback) {
		AON.start();
		fsa.fillMod2002021AccountingData(occam, mod200, data,
				new AsyncCallbackWrapper<Mod2002021>(callback));
	}

	@Override
	public void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(occam,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
}
