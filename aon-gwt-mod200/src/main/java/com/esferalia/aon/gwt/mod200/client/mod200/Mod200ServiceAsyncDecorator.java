package com.esferalia.aon.gwt.mod200.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod200ServiceAsyncDecorator implements Mod200ServiceAsync {

	private Mod200ServiceAsync fsa;

	public Mod200ServiceAsyncDecorator(Mod200ServiceAsync Mod200ServiceAsync) {
		this.fsa = Mod200ServiceAsync;
	}
	@Override
	public void getMod200(Occam occam, Integer id, AsyncCallback<Mod200> asyncCallback) {
		AON.start();
		fsa.getMod200(occam, id, new AsyncCallbackWrapper<Mod200>(asyncCallback));
	}
	@Override
	public void getMod200s(Occam occam, AsyncCallback<LinkedList<Mod200>> asyncCallback) {
		AON.start();
		fsa.getMod200s(occam, new AsyncCallbackWrapper<LinkedList<Mod200>>(asyncCallback));
	}
	@Override
	public void saveComments(Occam occam, Mod200 mod200, AsyncCallback<Mod200> asyncCallback) {
		AON.start();
		fsa.saveComments(occam, mod200, new AsyncCallbackWrapper<Mod200>(asyncCallback));
	}
}
