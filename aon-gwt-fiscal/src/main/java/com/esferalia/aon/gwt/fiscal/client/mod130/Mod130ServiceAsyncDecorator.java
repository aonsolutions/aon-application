package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod130ServiceAsyncDecorator implements Mod130ServiceAsync {

	private Mod130ServiceAsync fsa;

	public Mod130ServiceAsyncDecorator(Mod130ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod130(String domainName, String user, int domain, int id, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.getMod130(domainName, user, domain, id, new AsyncCallbackWrapper<Mod130>(callback));
	}

	@Override
	public void getMod130s(String domainName, String user, int domain, AsyncCallback<LinkedList<Mod130>> callback) {
		AON.start();
		fsa.getMod130s(domainName, user, domain, new AsyncCallbackWrapper<LinkedList<Mod130>>(callback));
	}

	@Override
	public void calculate(String domainName, String user, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.calculate(domainName, user, mod130, new AsyncCallbackWrapper<Mod130>(callback));
	}

	@Override
	public void save(String domainName, String user, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.save(domainName, user, mod130, new AsyncCallbackWrapper<Mod130>(callback));
	}
	
	@Override
	public void saveComments(String domainName, String user, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.saveComments(domainName, user, mod130, new AsyncCallbackWrapper<Mod130>(callback));
	}

	@Override
	public void initializeForFinish(String domainName, String user, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, user, mod130, new AsyncCallbackWrapper<Mod130>(callback));
	}
	
	@Override
	public void markAsFinished(String domainName, String user, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.markAsFinished(domainName, user, mod130, new AsyncCallbackWrapper<Mod130>(callback));
	}
	
	@Override
	public void markAsSent(String domainName, String user, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.markAsSent(domainName, user, mod130, new AsyncCallbackWrapper<Mod130>(callback));
	}

	@Override
	public void markAsPending(String domainName, String user, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.markAsPending(domainName, user, mod130, new AsyncCallbackWrapper<Mod130>(callback));
	}

	@Override
	public void initialize(String domainName, String user, int currentDomain, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.initialize(domainName, user, currentDomain,mod130,new AsyncCallbackWrapper<Mod130>(callback));

	}

	@Override
	public void create(String domainName, String user, int currentDomain, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.create(domainName, user, currentDomain, mod130,new AsyncCallbackWrapper<Mod130>(callback));

	}

	@Override
	public void delete(String domainName, String user, Mod130 mod130, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, user, mod130, new AsyncCallbackWrapper<Void>(callback));
	}
	@Override
	public void getInfo(String domainName, String user, int domain, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, user, domain, mod130, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}
	
	@Override
	public void mathExpression(String expression, AsyncCallback<Double> callback) {
		AON.start();
		fsa.mathExpression(expression,new AsyncCallbackWrapper<Double>(callback));
	}
}
