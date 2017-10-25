package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod303ServiceAsyncDecorator implements Mod303ServiceAsync {

	private Mod303ServiceAsync fsa;

	public Mod303ServiceAsyncDecorator(Mod303ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// ---------------------------------------------------------------MODELO 303

	@Override
	public void getMod303(String domainName, int domain, int id,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.getMod303(domainName, domain, id, new AsyncCallbackWrapper<Mod303>(
				callback));
	}

	@Override
	public void getMod303s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod303>> callback) {
		AON.start();
		fsa.getMod303s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod303>>(callback));
	}

	@Override
	public void calculate(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.calculate(domainName, mod303,
				new AsyncCallbackWrapper<Mod303>(callback));
	}

	@Override
	public void save(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.save(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}
	
	@Override
	public void saveComments(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.saveComments(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}

	@Override
	public void initializeForFinish(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}
	
	@Override
	public void markAsFinished(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.markAsFinished(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}
	
	@Override
	public void markAsPending(String domainName, Mod303 mod303,
			AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.markAsPending(domainName, mod303, new AsyncCallbackWrapper<Mod303>(
				callback));
	}

	@Override
	public void initialize(String domainName, int currentDomain,
			Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.initialize(domainName, currentDomain,mod303,new AsyncCallbackWrapper<Mod303>(callback));

	}

	@Override
	public void create(String domainName, int currentDomain,
			Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.create(domainName, currentDomain, mod303,new AsyncCallbackWrapper<Mod303>(callback));

	}

	@Override
	public void declarationChanged(String domainName, int domain, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.declarationChanged(domainName, domain, mod303,new AsyncCallbackWrapper<Mod303>(callback));
	}

	@Override
	public void delete(String domainName, Mod303 mod303,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, mod303, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void getInfo(String domainName, int domain, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod303, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}

	@Override
	public void importMod303(String domainName, int domain, AsyncCallback<Void> callback) {
		AON.start();
		fsa.importMod303(domainName, domain, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void markAsSent(String domainName, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.markAsSent(domainName, mod303, new AsyncCallbackWrapper<Mod303>(callback));
	}
}
