package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model190ServiceAsyncDecorator implements Model190ServiceAsync {

	private Model190ServiceAsync fsa;

	public Model190ServiceAsyncDecorator(Model190ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod190s(Occam occam, AsyncCallback<LinkedList<Mod190>> callback) {
		AON.start();
		fsa.getMod190s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod190(Occam occam, Integer id, AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.getMod190(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod190 mod190, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod190,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod190 mod190,AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.save(occam, mod190,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam,Integer year, AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.initialize(occam, year,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getDetail(Occam occam, Integer id, AsyncCallback<Mod190Detail> callback) {
		AON.start();
		fsa.getDetail(occam, id,new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod190 mod190,AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.saveComments(occam, mod190, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void changeStatus(Occam occam, Mod190 mod190, FiscalStatus newStatus,AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.changeStatus(occam, mod190, newStatus, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void duplicate(Occam occam, Mod190 mod190, AsyncCallback<Mod190> callback) {
		AON.start();
		fsa.duplicate(occam, mod190, new AsyncCallbackWrapper<>(callback));
	}
	

}
