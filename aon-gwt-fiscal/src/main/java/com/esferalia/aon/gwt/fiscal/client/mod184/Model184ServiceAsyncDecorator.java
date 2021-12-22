package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model184ServiceAsyncDecorator implements Model184ServiceAsync {

	private Model184ServiceAsync fsa;

	public Model184ServiceAsyncDecorator(Model184ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void delete(Occam occam, Mod184 mod184, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod184, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod184 mod184, AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.save(occam, mod184, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod184s(Occam occam, AsyncCallback<LinkedList<Mod184>> callback) {
		AON.start();
		fsa.getMod184s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, Integer year, AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.initialize(occam, year, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void get(Occam occam, Integer id, AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.get(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveComments(Occam occam, Mod184 mod184, AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.saveComments(occam, mod184, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void changeStatus(Occam occam, Mod184 mod184, FiscalStatus newStatus, AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.changeStatus(occam, mod184, newStatus, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void duplicate(Occam occam, Mod184 mod184, AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.duplicate(occam, mod184, new AsyncCallbackWrapper<>(callback));
		
	}
}
