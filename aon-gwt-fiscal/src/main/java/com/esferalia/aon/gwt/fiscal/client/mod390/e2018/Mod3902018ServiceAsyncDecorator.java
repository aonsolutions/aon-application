package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod3902018ServiceAsyncDecorator implements Mod3902018ServiceAsync {

	private Mod3902018ServiceAsync fsa;

	public Mod3902018ServiceAsyncDecorator(Mod3902018ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void get(Occam occam,Mod390 mod390, AsyncCallback<Mod3902018> callback) {
		AON.start();
		fsa.get(occam, mod390, new AsyncCallbackWrapper<Mod3902018>(callback));
	}

	@Override
	public void save(Occam occam, Mod3902018 mod390, AsyncCallback<Mod3902018> callback) {
		AON.start();
		fsa.save(occam,mod390, new AsyncCallbackWrapper<Mod3902018>(callback));
	}

	@Override
	public void delete(Occam occam,Mod3902018 mod390, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam,mod390,new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void changeStatus(Occam occam,Mod3902018 mod390, FiscalStatus status, AsyncCallback<Mod3902018> callback) {
		AON.start();
		fsa.changeStatus(occam, mod390, status,new AsyncCallbackWrapper<Mod3902018>(callback));
	}

}
