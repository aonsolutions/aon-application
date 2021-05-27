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
	public void getMod202(String domainName,String user, int domain, int id, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.getMod202(domainName, user, domain, id, new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void getMod202s(String domainName,String user, int domain,AsyncCallback<LinkedList<Mod202>> callback) {
		AON.start();
		fsa.getMod202s(domainName, user, domain,new AsyncCallbackWrapper<LinkedList<Mod202>>(callback));
	}

	@Override
	public void calculate(String domainName,String user, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.calculate(domainName, user, mod202, new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void save(String domainName,String user, Mod202 mod202,AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.save(domainName, user, mod202, new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void initialize(String domainName,String user, int currentDomain,Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.initialize(domainName, user, currentDomain, mod202,new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void delete(String domainName,String user, Mod202 mod202, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, user, mod202, new AsyncCallbackWrapper<Void>(callback));
	}
	@Override
	public void saveComments(String domainName,String user, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.saveComments(domainName, user, mod202, new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void initializeForFinish(String domainName,String user, Mod202 mod202,AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, user, mod202, new AsyncCallbackWrapper<Mod202>(callback));
	}
	
	@Override
	public void markAsFinished(String domainName,String user, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsFinished(domainName, user, mod202, new AsyncCallbackWrapper<Mod202>(callback));
	}
	
	@Override
	public void markAsSent(String domainName,String user, Mod202 mod202,AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsSent(domainName, user, mod202, new AsyncCallbackWrapper<Mod202>(callback));
	}
	
	@Override
	public void markAsCustomerCheck(String domainName, String user, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsCustomerCheck(domainName, user, mod202, new AsyncCallbackWrapper<Mod202>(callback));
	}

	@Override
	public void markAsPending(String domainName,String user, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsPending(domainName, user, mod202, new AsyncCallbackWrapper<Mod202>(callback));
	}
	@Override
	public void create(String domainName,String user, int currentDomain,Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.create(domainName, user, currentDomain, mod202,new AsyncCallbackWrapper<Mod202>(callback));

	}
	@Override
	public void getInfo(String domainName,String user, int domain, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey,
			AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, user, domain, mod202, script, infoKey,new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void mathExpression(String expression, AsyncCallback<Double> callback) {
		AON.start();
		fsa.mathExpression(expression,new AsyncCallbackWrapper<Double>(callback));
	}
}
