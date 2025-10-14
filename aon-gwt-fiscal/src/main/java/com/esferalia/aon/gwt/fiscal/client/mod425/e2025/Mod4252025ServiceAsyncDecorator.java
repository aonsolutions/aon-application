package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod4252025ServiceAsyncDecorator implements Mod4252025ServiceAsync {

	private Mod4252025ServiceAsync fsa;

	public Mod4252025ServiceAsyncDecorator(Mod4252025ServiceAsync mod425ServiceAsync) {
		this.fsa = mod425ServiceAsync;
	}

	@Override
	public void get(Occam occam, Mod390 mod425, AsyncCallback<Mod4252025> callback) {
		AON.start();
		fsa.get(occam, mod425, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod4252025 mod425, AsyncCallback<Mod4252025> callback) {
		AON.start();
		fsa.save(occam, mod425, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod4252025 mod425, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod425, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void changeStatus(Occam occam, Mod4252025 mod425, FiscalStatus status, AsyncCallback<Mod4252025> callback) {
		AON.start();
		fsa.changeStatus(occam, mod425, status,new AsyncCallbackWrapper<>(callback));
	}

}
