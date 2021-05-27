package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod3902018ServiceAsyncDecorator implements Mod3902018ServiceAsync {

	private Mod3902018ServiceAsync fsa;

	public Mod3902018ServiceAsyncDecorator(Mod3902018ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod3902018(String domainName, Integer domain, String user,Mod390 mod390,
			AsyncCallback<Mod3902018> callback) {
		AON.start();
		fsa.getMod3902018(domainName, domain, user, mod390, new AsyncCallbackWrapper<Mod3902018>(
				callback));
	}

	@Override
	public void saveMod3902018(String domainName, Integer domain, String user, Mod3902018 mod390,
			AsyncCallback<Mod3902018> callback) {
		AON.start();
		fsa.saveMod3902018(domainName, domain, user,mod390,
				new AsyncCallbackWrapper<Mod3902018>(callback));
	}

	@Override
	public void deleteMod3902018(String domainName, Integer domain, String user,Mod3902018 mod390,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod3902018(domainName, domain, user,mod390,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void changeStatus(String domainName, String user,Mod3902018 mod390, FiscalStatus status,
			AsyncCallback<Mod3902018> callback) {
		AON.start();
		fsa.changeStatus(domainName, user, mod390, status,new AsyncCallbackWrapper<Mod3902018>(callback));
	}

	@Override
	public void presentationFile(String domainName, Integer domainId, String user, Integer id,
			AsyncCallback<Integer> callback) {
		
	}

}
