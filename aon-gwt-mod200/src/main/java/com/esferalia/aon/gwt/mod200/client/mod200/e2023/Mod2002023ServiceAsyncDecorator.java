package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002023ServiceAsyncDecorator implements Mod2002023ServiceAsync {

	private Mod2002023ServiceAsync fsa;

	public Mod2002023ServiceAsyncDecorator(Mod2002023ServiceAsync Mod2002023ServiceAsync) {
		this.fsa = Mod2002023ServiceAsync;
	}

	@Override
	public void createMod2002023(Occam occam, int year,
			AsyncCallback<Mod2002023> callback) {
		AON.start();
		fsa.createMod2002023(occam, year,
				new AsyncCallbackWrapper<Mod2002023>(callback));
	}

	@Override
	public void initializeMod2002023(Occam occam,
			Mod2002023 mod200, AsyncCallback<Mod2002023> callback) {
		AON.start();
		fsa.initializeMod2002023(occam, mod200,
				new AsyncCallbackWrapper<Mod2002023>(callback));
	}

	@Override
	public void calculateMod2002023(Mod2002023 mod200,
			AsyncCallback<Mod2002023> callback) {
		AON.start();
		fsa.calculateMod2002023(mod200, new AsyncCallbackWrapper<Mod2002023>(
				callback));
	}

	@Override
	public void deleteMod2002023(Occam occam, Mod2002023 mod200,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002023(occam, mod200,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getMod2002023ById(Occam occam, int id,
			AsyncCallback<Mod2002023> callback) {
		AON.start();
		fsa.getMod2002023ById(occam, id, 
				new AsyncCallbackWrapper<Mod2002023>(callback));
	}

	@Override
	public void saveMod2002023(Occam occam, 
			Mod2002023 mod200, AsyncCallback<Mod2002023> callback) {
		AON.start();
		fsa.saveMod2002023(occam, mod200, 
				new AsyncCallbackWrapper<Mod2002023>(callback));

	}

	@Override
	public void fillMod2002023AccountingData(Occam occam, Mod2002023 mod200, String data,
			AsyncCallback<Mod2002023> callback) {
		AON.start();
		fsa.fillMod2002023AccountingData(occam, mod200, data,
				new AsyncCallbackWrapper<Mod2002023>(callback));
	}

	@Override
	public void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(occam,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
}
