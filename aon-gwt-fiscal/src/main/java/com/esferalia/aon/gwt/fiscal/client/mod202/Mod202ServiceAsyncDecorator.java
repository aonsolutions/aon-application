package com.esferalia.aon.gwt.fiscal.client.mod202;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
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
	public void getMod202(Occam occam, int id, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.getMod202(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod202s(Occam occam,AsyncCallback<LinkedList<Mod202>> callback) {
		AON.start();
		fsa.getMod202s(occam,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void calculate(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.calculate(occam, mod202, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod202 mod202,AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.save(occam, mod202, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam,Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.initialize(occam, mod202,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod202 mod202, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod202, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void saveComments(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.saveComments(occam, mod202, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initializeForFinish(Occam occam, Mod202 mod202,AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.initializeForFinish(occam, mod202, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsFinished(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsFinished(occam, mod202, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsSent(Occam occam, Mod202 mod202,AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsSent(occam, mod202, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsCustomerCheck(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsCustomerCheck(occam, mod202, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsPending(Occam occam, Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.markAsPending(occam, mod202, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void create(Occam occam,Mod202 mod202, AsyncCallback<Mod202> callback) {
		AON.start();
		fsa.create(occam, mod202,new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInfo(Occam occam, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod202, script, infoKey,new AsyncCallbackWrapper<>(callback));
	}

}
