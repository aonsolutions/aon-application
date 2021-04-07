package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod131ServiceAsyncDecorator implements Mod131ServiceAsync {

	private Mod131ServiceAsync fsa;

	public Mod131ServiceAsyncDecorator(Mod131ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod131(String domainName, String user, int domain, int id, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.getMod131(domainName, user, domain, id, new AsyncCallbackWrapper<Mod131>(callback));
	}

	@Override
	public void getMod131s(String domainName, String user, int domain, AsyncCallback<LinkedList<Mod131>> callback) {
		AON.start();
		fsa.getMod131s(domainName, user, domain, new AsyncCallbackWrapper<LinkedList<Mod131>>(callback));
	}

	@Override
	public void calculate(String domainName, String user, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.calculate(domainName, user, mod131, new AsyncCallbackWrapper<Mod131>(callback));
	}
	@Override
	public void calculateActivity(String domainName, String user, int domain, Mod131 mod131,Mod131Activity activity, AsyncCallback<Mod131Activity> callback) {
		AON.start();
		fsa.calculateActivity(domainName, user, domain, mod131, activity, new AsyncCallbackWrapper<Mod131Activity>(callback));
	}

	@Override
	public void save(String domainName, String user, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.save(domainName, user, mod131, new AsyncCallbackWrapper<Mod131>(callback));
	}
	
	@Override
	public void saveComments(String domainName, String user, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.saveComments(domainName, user, mod131, new AsyncCallbackWrapper<Mod131>( callback));
	}

	@Override
	public void initializeForFinish(String domainName, String user, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, user, mod131, new AsyncCallbackWrapper<Mod131>(callback));
	}
	
	@Override
	public void markAsFinished(String domainName, String user, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.markAsFinished(domainName, user, mod131, new AsyncCallbackWrapper<Mod131>(callback));
	}
	
	@Override
	public void markAsSent(String domainName, String user, Mod131 mod131,AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.markAsSent(domainName, user, mod131, new AsyncCallbackWrapper<Mod131>(callback));
	}
	@Override
	public void markAsCustomerCheck(String domainName, String user, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.markAsCustomerCheck(domainName, user, mod131, new AsyncCallbackWrapper<Mod131>(callback));
	}
	@Override
	public void markAsPending(String domainName, String user, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.markAsPending(domainName, user, mod131, new AsyncCallbackWrapper<Mod131>(callback));
	}

	@Override
	public void initialize(String domainName, String user, int currentDomain, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.initialize(domainName, user, currentDomain,mod131,new AsyncCallbackWrapper<Mod131>(callback));

	}

	@Override
	public void create(String domainName, String user, int currentDomain, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.create(domainName, user, currentDomain, mod131,new AsyncCallbackWrapper<Mod131>(callback));

	}

	@Override
	public void delete(String domainName, String user, Mod131 mod131, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, user, mod131, new AsyncCallbackWrapper<Void>(callback));
	}
	@Override
	public void getInfo(String domainName, String user, int domain, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, user, domain, mod131, script, infoKey,new AsyncCallbackWrapper<String>(callback));
		
	}

	@Override
	public void mathExpression(String expression, AsyncCallback<Double> callback) {
		AON.start();
		fsa.mathExpression(expression,new AsyncCallbackWrapper<Double>(callback));
	}

	@Override
	public void presentationFile(String domainName, Integer domainId, String user, Integer id, AsyncCallback<Integer> callback) {
		
	}
}
