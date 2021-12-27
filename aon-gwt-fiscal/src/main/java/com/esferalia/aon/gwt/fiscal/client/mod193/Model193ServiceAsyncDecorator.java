package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model193ServiceAsyncDecorator implements Model193ServiceAsync {

	private Model193ServiceAsync fsa;

	public Model193ServiceAsyncDecorator(Model193ServiceAsync mod193ServiceAsync) {
		this.fsa = mod193ServiceAsync;
	}

	@Override
	public void getMod193s(Occam occam, AsyncCallback<LinkedList<Mod193>> callback) {
		AON.start();
		fsa.getMod193s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void get(Occam occam, Integer id, AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.get(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod193 mod193,AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod193,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod193 mod193,AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.save(occam, mod193,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, Integer year, AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.initialize(occam, year, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveComments(Occam occam, Mod193 mod193,AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.saveComments(occam, mod193, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void changeStatus(Occam occam, Mod193 mod193, FiscalStatus newStatus,AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.changeStatus(occam, mod193, newStatus, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void duplicate(Occam occam, Mod193 mod193, AsyncCallback<Mod193> callback) {
		AON.start();
		fsa.duplicate(occam, mod193, new AsyncCallbackWrapper<>(callback));
	}
}
