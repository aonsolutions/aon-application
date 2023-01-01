package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002022ServiceAsyncDecorator implements Mod2002022ServiceAsync {

	private Mod2002022ServiceAsync fsa;

	public Mod2002022ServiceAsyncDecorator(Mod2002022ServiceAsync Mod2002022ServiceAsync) {
		this.fsa = Mod2002022ServiceAsync;
	}

	@Override
	public void createMod2002022(Occam occam, int year,
			AsyncCallback<Mod2002022> callback) {
		AON.start();
		fsa.createMod2002022(occam, year,
				new AsyncCallbackWrapper<Mod2002022>(callback));
	}

	@Override
	public void initializeMod2002022(Occam occam,
			Mod2002022 mod200, AsyncCallback<Mod2002022> callback) {
		AON.start();
		fsa.initializeMod2002022(occam, mod200,
				new AsyncCallbackWrapper<Mod2002022>(callback));
	}

	@Override
	public void calculateMod2002022(Mod2002022 mod200,
			AsyncCallback<Mod2002022> callback) {
		AON.start();
		fsa.calculateMod2002022(mod200, new AsyncCallbackWrapper<Mod2002022>(
				callback));
	}

	@Override
	public void deleteMod2002022(Occam occam, Mod2002022 mod200,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002022(occam, mod200,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getMod2002022ById(Occam occam, int id,
			AsyncCallback<Mod2002022> callback) {
		AON.start();
		fsa.getMod2002022ById(occam, id, 
				new AsyncCallbackWrapper<Mod2002022>(callback));
	}

	@Override
	public void saveMod2002022(Occam occam, 
			Mod2002022 mod200, AsyncCallback<Mod2002022> callback) {
		AON.start();
		fsa.saveMod2002022(occam, mod200, 
				new AsyncCallbackWrapper<Mod2002022>(callback));

	}

	@Override
	public void fillMod2002022AccountingData(Occam occam, Mod2002022 mod200, String data,
			AsyncCallback<Mod2002022> callback) {
		AON.start();
		fsa.fillMod2002022AccountingData(occam, mod200, data,
				new AsyncCallbackWrapper<Mod2002022>(callback));
	}

	@Override
	public void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(occam,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
}
