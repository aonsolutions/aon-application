package com.esferalia.aon.gwt.fiscal.client.mod369;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model369ServiceAsyncDecorator implements Model369ServiceAsync {

	private Model369ServiceAsync fsa;

	public Model369ServiceAsyncDecorator(Model369ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void delete(Occam occam, Mod369 mod369, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod369, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod369 mod369, AsyncCallback<Mod369> callback) {
		AON.start();
		fsa.save(occam, mod369, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod369s(Occam occam, AsyncCallback<LinkedList<Mod369>> callback) {
		AON.start();
		fsa.getMod369s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, Integer year, Period period, AsyncCallback<Mod369> callback) {
		AON.start();
		fsa.initialize(occam, year, period, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void get(Occam occam, Integer id, AsyncCallback<Mod369> callback) {
		AON.start();
		fsa.get(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveComments(Occam occam, Mod369 mod369, AsyncCallback<Mod369> callback) {
		AON.start();
		fsa.saveComments(occam, mod369, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void changeStatus(Occam occam, Mod369 mod369, FiscalStatus newStatus, AsyncCallback<Mod369> callback) {
		AON.start();
		fsa.changeStatus(occam, mod369, newStatus, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void duplicate(Occam occam, Mod369 mod369, AsyncCallback<Mod369> callback) {
		AON.start();
		fsa.duplicate(occam, mod369, new AsyncCallbackWrapper<>(callback));
		
	}
}
