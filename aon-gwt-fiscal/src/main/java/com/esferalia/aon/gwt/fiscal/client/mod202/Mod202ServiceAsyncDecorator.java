package com.esferalia.aon.gwt.fiscal.client.mod202;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod202ServiceAsyncDecorator implements Mod202ServiceAsync {

	private Mod202ServiceAsync fsa;

	public Mod202ServiceAsyncDecorator(Mod202ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod202(String domainName, int domain, int id,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.getMod202(domainName, domain, id, new AsyncCallbackWrapper<Mod202>(
				callback));
	}

	@Override
	public void getMod202s(String domainName, int domain,
			AsyncCallback<LinkedList<Mod202>> callback) {
		AON.start();
		fsa.getMod202s(domainName, domain,
				new AsyncCallbackWrapper<LinkedList<Mod202>>(callback));
	}

	@Override
	public void calculate(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.calculate(domainName, mod202,
				new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void save(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.save(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}

	@Override
	public void initialize(String domainName, int currentDomain,
			Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.initialize(domainName, currentDomain, mod202,
				new AsyncCallbackWrapper<Mod202>(callback));

	}

	@Override
	public void delete(String domainName, Mod202 mod202,
			AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, mod202, new AsyncCallbackWrapper<Void>(
				callback));
	}
	@Override
	public void saveComments(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.saveComments(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}

	@Override
	public void initializeForFinish(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}
	
	@Override
	public void markAsFinished(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsFinished(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}
	
	@Override
	public void markAsSent(String domainName, Mod202 mod202,AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsSent(domainName, mod202, new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void markAsPending(String domainName, Mod202 mod202,
			AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsPending(domainName, mod202, new AsyncCallbackWrapper<Mod202>(
				callback));
	}
	@Override
	public void create(String domainName, int currentDomain,Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.create(domainName, currentDomain, mod202,new AsyncCallbackWrapper<Mod202>(callback));

	}
	@Override
	public void getInfo(String domainName, int domain, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, domain, mod202, script, infoKey,new AsyncCallbackWrapper<String>(callback));
	}

}
