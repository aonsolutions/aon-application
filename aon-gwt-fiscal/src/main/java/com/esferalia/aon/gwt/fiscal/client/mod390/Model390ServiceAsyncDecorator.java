package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model390ServiceAsyncDecorator implements Model390ServiceAsync {

	private Model390ServiceAsync fsa;

	public Model390ServiceAsyncDecorator(Model390ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod390s(String domainName, Integer domain,
			AsyncCallback<LinkedList<Mod390>> callback) {
		AON.start();
		fsa.getMod390s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod390>>(callback));
	}

	@Override
	public void create(String domainName, int domain, Mod390 mod390, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.create(domainName, domain, mod390, new AsyncCallbackWrapper<Mod390>(callback));
		
	}

	@Override
	public void initialize(String domainName, int domain, int year, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.initialize(domainName, domain, year, new AsyncCallbackWrapper<Mod390>(callback));
	}

	@Override
	public void saveComments(String domainName, Mod390 mod390, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.saveComments(domainName, mod390, new AsyncCallbackWrapper<Mod390>(callback));
	}
}
