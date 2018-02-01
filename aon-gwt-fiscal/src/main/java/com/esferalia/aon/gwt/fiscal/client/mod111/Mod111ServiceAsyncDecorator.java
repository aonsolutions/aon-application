package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod111ServiceAsyncDecorator implements Mod111ServiceAsync {

	private Mod111ServiceAsync fsa;

	public Mod111ServiceAsyncDecorator(Mod111ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	@Override
	public void getMod111(String domainName, String userLogin, int domain, int id, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.getMod111(domainName, userLogin ,domain, id, new AsyncCallbackWrapper<Mod111>(callback));
	}

	@Override
	public void getMod111s(String domainName, String userLogin, int domain, AsyncCallback<LinkedList<Mod111>> callback) {
		AON.start();
		fsa.getMod111s(domainName, userLogin, domain, new AsyncCallbackWrapper<LinkedList<Mod111>>(callback));
	}

	@Override
	public void calculate(String domainName, String userLogin, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.calculate(domainName, userLogin, mod111, new AsyncCallbackWrapper<Mod111>(callback));
	}

	@Override
	public void save(String domainName, String userLogin, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.save(domainName, userLogin, mod111, new AsyncCallbackWrapper<Mod111>(callback));
	}
	
	@Override
	public void saveComments(String domainName, String userLogin, Mod111 mod111,AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.saveComments(domainName, userLogin, mod111, new AsyncCallbackWrapper<Mod111>(callback));
	}

	@Override
	public void initializeForFinish(String domainName, String userLogin, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.initializeForFinish(domainName, userLogin, mod111, new AsyncCallbackWrapper<Mod111>(callback));
	}
	
	@Override
	public void markAsFinished(String domainName, String userLogin, Mod111 mod111,AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.markAsFinished(domainName, userLogin, mod111, new AsyncCallbackWrapper<Mod111>(callback));
	}
	
	@Override
	public void markAsPending(String domainName, String userLogin, Mod111 mod111,AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.markAsPending(domainName, userLogin, mod111, new AsyncCallbackWrapper<Mod111>(callback));
	}

	@Override
	public void markAsSent(String domainName, String userLogin, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.markAsSent(domainName, userLogin, mod111, new AsyncCallbackWrapper<Mod111>(callback));
	}

	@Override
	public void initialize(String domainName, String userLogin, int currentDomain,Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.initialize(domainName, userLogin, currentDomain,mod111,new AsyncCallbackWrapper<Mod111>(callback));

	}

	@Override
	public void create(String domainName, String userLogin, int currentDomain, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.create(domainName, userLogin, currentDomain, mod111,new AsyncCallbackWrapper<Mod111>(callback));
	}

	@Override
	public void delete(String domainName, String userLogin, Mod111 mod111, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(domainName, userLogin, mod111, new AsyncCallbackWrapper<Void>(callback));
	}
	@Override
	public void getInfo(String domainName, String userLogin, int domain, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(domainName, userLogin, domain, mod111, script, infoKey,new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void mathExpression(String expression, AsyncCallback<Double> callback) {
		AON.start();
		fsa.mathExpression(expression,new AsyncCallbackWrapper<Double>(callback));
	}

}
