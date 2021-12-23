package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model180ServiceAsyncDecorator implements Model180ServiceAsync {

	private Model180ServiceAsync fsa;

	public Model180ServiceAsyncDecorator(Model180ServiceAsync fsa) {
		this.fsa = fsa;
	}

	@Override
	public void delete(Occam occam, Mod180 mod180, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod180, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod180 mod180, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.save(occam, mod180, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod180s(Occam occam, AsyncCallback<LinkedList<Mod180>> callback) {
		AON.start();
		fsa.getMod180s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, Integer year, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.initialize(occam, year, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void get(Occam occam, Integer id, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.get(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getDetail(Occam occam, Integer id, AsyncCallback<Mod180Detail> callback) {
		AON.start();
		fsa.getDetail(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveComments(Occam occam, Mod180 mod180, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.saveComments(occam, mod180, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void changeStatus(Occam occam, Mod180 mod180, FiscalStatus newStatus, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.changeStatus(occam, mod180, newStatus, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void duplicate(Occam occam, Mod180 mod180, AsyncCallback<Mod180> callback) {
		AON.start();
		fsa.duplicate(occam, mod180, new AsyncCallbackWrapper<>(callback));
	}

}
