package com.esferalia.aon.gwt.fiscal.client.mod347;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model347ServiceAsyncDecorator implements Model347ServiceAsync {
	
	private Model347ServiceAsync fsa;

	public Model347ServiceAsyncDecorator(Model347ServiceAsync mod347ServiceAsync) {
		this.fsa = mod347ServiceAsync;
	}
	
	
	// ---------------------------------------------------------------MODELO 347
	@Override
	public void deleteMod347(String domainName, int domainId, Mod347 mod347,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.deleteMod347(domainName, domainId, mod347,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveMod347(String domainName, int domainId, Mod347 mod347,
			AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.saveMod347(domainName, domainId, mod347,
				new AsyncCallbackWrapper<Mod347>(callback));
	}

	@Override
	public void getMod347s(String domainName, int domainId,
			AsyncCallback<LinkedList<Mod347>> callback) {
		AON.start();
		fsa.getMod347s(domainName, domainId,
				new AsyncCallbackWrapper<LinkedList<Mod347>>(callback));
	}

	@Override
	public void initializeMod347(String domainName, Integer domain,
			AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.initializeMod347(domainName, domain, new AsyncCallbackWrapper<Mod347>(callback));
	}

	@Override
	public void getMod347(String domainName, int domainId, Integer id,
			AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.getMod347(domainName, domainId, id,
				new AsyncCallbackWrapper<Mod347>(callback));
	}

	@Override
	public void saveCommentsMod347(String domainName, Mod347 mod347,
			AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.saveCommentsMod347(domainName, mod347, new AsyncCallbackWrapper<Mod347>(
				callback));
	}

	@Override
	public void changeStatusMod347(String domainName, Mod347 mod347, FiscalStatus newStatus,
			AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.changeStatusMod347(domainName, mod347, newStatus, 
				new AsyncCallbackWrapper<Mod347>(callback));
	}
	
	@Override
	public void getInfo(String domainName, int domain, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod347, declared, infoKey, new AsyncCallbackWrapper<String>(callback));		
	}
	
	@Override
	public void duplicateNextYear(String domainName, Integer domain, Integer id, AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.duplicateNextYear(domainName, domain, id, new AsyncCallbackWrapper<Mod347>(callback));
	}

}
