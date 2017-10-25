package com.esferalia.aon.gwt.fiscal.client.mod115;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod115ServiceAsyncDecorator implements Mod115ServiceAsync {

	private Mod115ServiceAsync fsa;

	public Mod115ServiceAsyncDecorator(Mod115ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod115(String domainName, int domain, int id,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.getMod115(domainName, domain, id, new AsyncCallbackWrapper<Mod115>(
				callback));
	}

	@Override
	public void getMod115s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod115>> callback) {
		AON.start();
		fsa.getMod115s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod115>>(callback));
	}

	@Override
	public void calculate(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.calculate(domainName, mod115,
				new AsyncCallbackWrapper<Mod115>(callback));
	}

	@Override
	public void save(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.save(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}
	
	@Override
	public void saveComments(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.saveComments(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}

	@Override
	public void initializeForFinish(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}
	
	@Override
	public void markAsFinished(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.markAsFinished(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}
	
	@Override
	public void markAsPending(String domainName, Mod115 mod115,
			AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.markAsPending(domainName, mod115, new AsyncCallbackWrapper<Mod115>(
				callback));
	}

	@Override
	public void markAsSent(String domainName, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.markAsSent(domainName, mod115, new AsyncCallbackWrapper<Mod115>(callback));
	}

	@Override
	public void initialize(String domainName, int currentDomain,
			Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.initialize(domainName, currentDomain,mod115,new AsyncCallbackWrapper<Mod115>(callback));

	}

	@Override
	public void create(String domainName, int currentDomain,
			Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.create(domainName, currentDomain, mod115,new AsyncCallbackWrapper<Mod115>(callback));

	}

	@Override
	public void delete(String domainName, Mod115 mod115,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, mod115, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void getInfo(String domainName, int domain, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod115, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}

}
