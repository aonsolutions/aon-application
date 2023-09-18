package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
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
	public void getMod111(Occam occam, int id, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.getMod111(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod111s(Occam occam, AsyncCallback<LinkedList<Mod111>> callback) {
		AON.start();
		fsa.getMod111s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void calculate(Occam occam, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.calculate(occam, mod111, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.save(occam, mod111, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod111 mod111,AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.saveComments(occam, mod111, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initializeForFinish(Occam occam, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.initializeForFinish(occam, mod111, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsFinished(Occam occam, Mod111 mod111,AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.markAsFinished(occam, mod111, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsCustomerCheck(Occam occam, Mod111 mod111,AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.markAsCustomerCheck(occam, mod111, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsPending(Occam occam, Mod111 mod111,AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.markAsPending(occam, mod111, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsSent(Occam occam, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.markAsSent(occam, mod111, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam,Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.initialize(occam,mod111,new AsyncCallbackWrapper<>(callback));

	}

	@Override
	public void create(Occam occam, Mod111 mod111, AsyncCallback<Mod111> callback) {
		AON.start();
		fsa.create(occam, mod111,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod111 mod111, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod111, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void getInfo(Occam occam, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod111, script, infoKey,new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
}
