package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod3902015ServiceAsyncDecorator implements Mod3902015ServiceAsync {

	private Mod3902015ServiceAsync fsa;

	public Mod3902015ServiceAsyncDecorator(Mod3902015ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod3902015(String domainName, Integer domain, Mod390 mod390,
			AsyncCallback<Mod3902015> callback) {
		AON.start();
		fsa.getMod3902015(domainName, domain, mod390, new AsyncCallbackWrapper<Mod3902015>(
				callback));
	}

	@Override
	public void saveMod3902015(String domainName, Integer domain, Mod3902015 mod390,
			AsyncCallback<Mod3902015> callback) {
		AON.start();
		fsa.saveMod3902015(domainName, domain, mod390,
				new AsyncCallbackWrapper<Mod3902015>(callback));
	}

	@Override
	public void deleteMod3902015(String domainName, Integer domain, Mod3902015 mod390,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod3902015(domainName, domain, mod390,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void changeStatus(String domainName, Mod3902015 mod390, FiscalStatus status,
			AsyncCallback<Mod3902015> callback) {
		AON.start();
		fsa.changeStatus(domainName, mod390, status,new AsyncCallbackWrapper<Mod3902015>(callback));
	}

	@Override
	public void presentationFile(String domainName, Integer domainId, String user, Integer id,
			AsyncCallback<Integer> callback) {
		
	}

}
