package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod2002020ServiceAsyncDecorator implements Mod2002020ServiceAsync {

	private Mod2002020ServiceAsync fsa;

	public Mod2002020ServiceAsyncDecorator(Mod2002020ServiceAsync Mod2002020ServiceAsync) {
		this.fsa = Mod2002020ServiceAsync;
	}

	@Override
	public void createMod2002020(Occam occam, int year,
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.createMod2002020(occam, year,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void initializeNewMod2002020(Occam occam,
			Mod2002020 mod200, AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.initializeNewMod2002020(occam, mod200,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void initializeMod2002020(Occam occam,
			Mod2002020 mod200, AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.initializeMod2002020(occam, mod200,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void getMod2002020ByYear(Occam occam, int year, 
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.getMod2002020ByYear(occam, year, 
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
	public void deleteMod2002020(Occam occam, int id,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod2002020(occam, id,
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
	public void getMod2002020ById(Occam occam, int id,
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.getMod2002020ById(occam, id, 
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void saveMod2002020(Occam occam, 
			Mod2002020 mod200, AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.saveMod2002020(occam, mod200, 
				new AsyncCallbackWrapper<Mod2002020>(callback));

	}

//	@Override
//	public void validateMod2002020(Mod2002020 mod200,
//			AsyncCallback<Mod2002020> callback) {
//		AON.start();
//		fsa.validateMod2002020(mod200, new AsyncCallbackWrapper<Mod2002020>(
//				callback));
//	}

	@Override
	public void importMod2002019(Occam occam,
			Mod2002020 mod200, AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.importMod2002019(occam, mod200,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void fillMod2002020AccountingData(Occam occam, Mod2002020 mod200, String data,
			AsyncCallback<Mod2002020> callback) {
		AON.start();
		fsa.fillMod2002020AccountingData(occam, mod200, data,
				new AsyncCallbackWrapper<Mod2002020>(callback));
	}

	@Override
	public void getCompanyBanks(Occam occam, AsyncCallback<LinkedList<CompanyBank>> callback) {
		AON.start();
		fsa.getCompanyBanks(occam,
				new AsyncCallbackWrapper<LinkedList<CompanyBank>>(callback));
	}
}
