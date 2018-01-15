package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model184ServiceAsyncDecorator implements Model184ServiceAsync {

	private Model184ServiceAsync fsa;

	public Model184ServiceAsyncDecorator(Model184ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void deleteMod184(String domainName, int domain, Mod184 mod184,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod184(domainName, domain, mod184,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod184(String domainName, int domain, Mod184 mod184,
			AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.saveMod184(domainName, domain, mod184,
				new AsyncCallbackWrapper<Mod184>(callback));
	}

	@Override
	public void getMod184s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod184>> callback) {
		AON.start();
		fsa.getMod184s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod184>>(callback));
	}

	@Override
	public void initializeMod184(String domainName, Integer domain,
			Integer year, AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.initializeMod184(domainName, domain, year,
				new AsyncCallbackWrapper<Mod184>(callback));
	}

	@Override
	public void getMod184(String domainName, int domain, Integer id,
			AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.getMod184(domainName, domain, id, new AsyncCallbackWrapper<Mod184>(
				callback));
	}

	@Override
	public void saveCommentsMod184(String domainName, Mod184 mod184,
			AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.saveCommentsMod184(domainName, mod184, new AsyncCallbackWrapper<Mod184>(
				callback));
	}

	@Override
	public void changeStatusMod184(String domainName, Mod184 mod184, FiscalStatus newStatus,
			AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.changeStatusMod184(domainName, mod184, newStatus, 
				new AsyncCallbackWrapper<Mod184>(callback));
	}

	@Override
	public void duplicateNextYear(String domainName, Integer domain, Integer id, AsyncCallback<Mod184> callback) {
		AON.start();
		fsa.duplicateNextYear(domainName, domain, id, new AsyncCallbackWrapper<Mod184>(callback));
		
	}
}
