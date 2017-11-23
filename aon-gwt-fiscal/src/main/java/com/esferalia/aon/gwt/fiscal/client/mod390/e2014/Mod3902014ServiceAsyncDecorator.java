package com.esferalia.aon.gwt.fiscal.client.mod390.e2014;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod3902014ServiceAsyncDecorator implements Mod3902014ServiceAsync {

	private Mod3902014ServiceAsync fsa;

	public Mod3902014ServiceAsyncDecorator(Mod3902014ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod3902014(String domainName, Integer domain, Integer id,
			AsyncCallback<Mod3902014> callback) {
		AON.start();
		fsa.getMod3902014(domainName, domain, id, new AsyncCallbackWrapper<Mod3902014>(
				callback));
	}

	@Override
	public void saveMod3902014(String domainName, Integer domain, Mod3902014 mod390,
			AsyncCallback<Mod3902014> callback) {
		AON.start();
		fsa.saveMod3902014(domainName, domain, mod390,
				new AsyncCallbackWrapper<Mod3902014>(callback));
	}

	@Override
	public void deleteMod3902014(String domainName, Integer domain, Mod3902014 mod390,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod3902014(domainName, domain, mod390,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void initializeMod3902014(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod3902014> callback) {
		AON.start();
		fsa.initializeMod3902014(domainName, domain, year,
				new AsyncCallbackWrapper<Mod3902014>(callback));
	}

}
