package com.esferalia.aon.gwt.fiscal.client.mod421;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod421ServiceAsyncDecorator implements Mod421ServiceAsync {

	private Mod421ServiceAsync fsa;

	public Mod421ServiceAsyncDecorator(Mod421ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// ---------------------------------------------------------------MODELO 421

	@Override
	public void getMod421(Occam occam, int id, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.getMod421(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod421s(Occam occam,AsyncCallback<LinkedList<Mod421>> callback) {
		AON.start();
		fsa.getMod421s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.initialize(occam,mod421,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void create(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.create(occam, mod421,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.save(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.saveComments(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void calculate(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.calculate(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initializeForFinish(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.initializeForFinish(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void delete(Occam occam, Mod421 mod421, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInfo(Occam occam, Mod421 mod421, IModelScript<Mod421Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod421, script, infoKey,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsPending(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.markAsPending(occam, mod421, new AsyncCallbackWrapper<>( callback));
	}

	@Override
	public void markAsFinished(Occam occam, Mod421 mod421,AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.markAsFinished(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsSent(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.markAsSent(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsCustomerCheck(Occam occam, Mod421 mod421,AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.markAsCustomerCheck(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}


	@Override
	public void doRecord(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.doRecord(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void unrecord(Occam occam, Mod421 mod421, AsyncCallback<Mod421> callback) {
		AON.start();
		fsa.unrecord(occam, mod421, new AsyncCallbackWrapper<>(callback));
	}
	
}
