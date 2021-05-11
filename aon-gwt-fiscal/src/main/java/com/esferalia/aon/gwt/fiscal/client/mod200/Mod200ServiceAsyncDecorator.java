package com.esferalia.aon.gwt.fiscal.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod200ServiceAsyncDecorator implements Mod200ServiceAsync {

	private Mod200ServiceAsync fsa;

	public Mod200ServiceAsyncDecorator(Mod200ServiceAsync Mod200ServiceAsync) {
		this.fsa = Mod200ServiceAsync;
	}
	@Override
	public void getMod200(String domainName, int domain, String user, Integer id, AsyncCallback<Mod200> asyncCallback) {
		AON.start();
		fsa.getMod200(domainName, domain, user, id, new AsyncCallbackWrapper<Mod200>(asyncCallback));
	}
	@Override
	public void getMod200s(String domainName, int domain, String user, AsyncCallback<LinkedList<Mod200>> asyncCallback) {
		AON.start();
		fsa.getMod200s(domainName, domain, user, new AsyncCallbackWrapper<LinkedList<Mod200>>(asyncCallback));
	}
}
