package com.esferalia.aon.gwt.fiscal.client.mod123;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
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
	public void getMod123(Occam occam, int id, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.getMod123(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod123s(Occam occam,AsyncCallback<LinkedList<Mod123>> callback) {
		AON.start();
		fsa.getMod123s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void calculate(Occam occam, Mod123 mod123,AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.calculate(occam, mod123,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod123 mod123,AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.save(occam, mod123, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.saveComments(occam, mod123, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initializeForFinish(Occam occam, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.initializeForFinish(occam, mod123, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsFinished(Occam occam, Mod123 mod123,AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.markAsFinished(occam, mod123, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsSent(Occam occam, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.markAsSent(occam, mod123, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsCustomerCheck(Occam occam, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.markAsCustomerCheck(occam, mod123, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void markAsPending(Occam occam, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.markAsPending(occam, mod123, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.initialize(occam,mod123,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void create(Occam occam, Mod123 mod123, AsyncCallback<Mod123> callback) {
		AON.start();
		fsa.create(occam, mod123,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod123 mod123, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod123, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInfo(Occam occam, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod123, script, infoKey,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<Invoice>(callback));
	}
}
