package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod123ServiceAsyncDecorator implements Mod123ServiceAsync {

	private Mod123ServiceAsync fsa;

	public Mod123ServiceAsyncDecorator(Mod123ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod123(String domainName, String userLogin, int domain, int id, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.getMod123(domainName, userLogin, domain, id, new AsyncCallbackWrapper<Mod123>(callback));
	}

	@Override
	public void getMod123s(String domainName, String userLogin, int domain,AsyncCallback<LinkedList<Mod123>> callback) {
		AON.start();
		fsa.getMod123s(domainName, userLogin, domain, new AsyncCallbackWrapper<LinkedList<Mod123>>(callback));
	}

	@Override
	public void calculate(String domainName, String userLogin, Mod123 mod123,AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.calculate(domainName, userLogin, mod123,new AsyncCallbackWrapper<Mod123>(callback));
	}

	@Override
	public void save(String domainName, String userLogin, Mod123 mod123,AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.save(domainName, userLogin, mod123, new AsyncCallbackWrapper<Mod123>(callback));
	}
	
	@Override
	public void saveComments(String domainName, String userLogin, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.saveComments(domainName, userLogin, mod123, new AsyncCallbackWrapper<Mod123>(callback));
	}

	@Override
	public void initializeForFinish(String domainName, String userLogin, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, userLogin, mod123, new AsyncCallbackWrapper<Mod123>(callback));
	}
	
	@Override
	public void markAsFinished(String domainName, String userLogin, Mod123 mod123,AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.markAsFinished(domainName, userLogin , mod123, new AsyncCallbackWrapper<Mod123>(callback));
	}
	
	@Override
	public void markAsSent(String domainName, String userLogin, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.markAsSent(domainName, userLogin, mod123, new AsyncCallbackWrapper<Mod123>(callback));
	}

	@Override
	public void markAsPending(String domainName, String userLogin, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.markAsPending(domainName, userLogin, mod123, new AsyncCallbackWrapper<Mod123>(callback));
	}

	@Override
	public void initialize(String domainName, String userLogin, int currentDomain, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.initialize(domainName, userLogin, currentDomain,mod123,new AsyncCallbackWrapper<Mod123>(callback));

	}

	@Override
	public void create(String domainName, String userLogin, int currentDomain, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.create(domainName, userLogin, currentDomain, mod123,new AsyncCallbackWrapper<Mod123>(callback));

	}

	@Override
	public void delete(String domainName, String userLogin, Mod123 mod123, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, userLogin, mod123, new AsyncCallbackWrapper<Void>(callback));
	}
	@Override
	public void getInfo(String domainName, String userLogin, int domain, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, userLogin, domain, mod123, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}

}
