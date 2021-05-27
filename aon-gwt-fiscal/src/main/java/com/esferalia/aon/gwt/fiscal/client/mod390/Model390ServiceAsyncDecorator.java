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
	public void getMod390(String domainName, Integer domain, String user, Integer id,
			AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.getMod390(domainName, domain, user,id,new AsyncCallbackWrapper<Mod390>(callback));
	}

	@Override
	public void getMod390s(String domainName, Integer domain, String user,
			AsyncCallback<LinkedList<Mod390>> callback) {
		AON.start();
		fsa.getMod390s(domainName, domain, user,
				new AsyncCallbackWrapper<LinkedList<Mod390>>(callback));
	}

	@Override
	public void create(String domainName, int domain, String user, Mod390 mod390, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.create(domainName, domain, user, mod390, new AsyncCallbackWrapper<Mod390>(callback));
		
	}

	@Override
	public void initialize(String domainName, int domain, String user, int year, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.initialize(domainName, domain, user, year, new AsyncCallbackWrapper<Mod390>(callback));
	}

	@Override
	public void saveComments(String domainName, String user, Mod390 mod390, AsyncCallback<Mod390> callback) {
		AON.start();
		fsa.saveComments(domainName, user, mod390, new AsyncCallbackWrapper<Mod390>(callback));
	}
}
