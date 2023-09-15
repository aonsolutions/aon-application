package com.esferalia.aon.gwt.fiscal.client.mod115;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod115ServiceAsyncDecorator implements Mod115ServiceAsync {

	private Mod115ServiceAsync fsa;

	public Mod115ServiceAsyncDecorator(Mod115ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}
	
	@Override
	public void getMod115(Occam occam, int id,AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.getMod115(occam, id, new AsyncCallbackWrapper<Mod115>(callback));
	}

	@Override
	public void getMod115s(Occam occam, AsyncCallback<LinkedList<Mod115>> callback) {
		AON.start();
		fsa.getMod115s(occam, new AsyncCallbackWrapper<LinkedList<Mod115>>(callback));
	}

	@Override
	public void calculate(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.calculate(occam, mod115,new AsyncCallbackWrapper<Mod115>(callback));
	}

	@Override
	public void save(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.save(occam, mod115, new AsyncCallbackWrapper<Mod115>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.saveComments(occam, mod115, new AsyncCallbackWrapper<Mod115>(callback));
	}

	@Override
	public void initializeForFinish(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.initializeForFinish(occam, mod115, new AsyncCallbackWrapper<Mod115>(callback));
	}
	
	@Override
	public void markAsFinished(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.markAsFinished(occam, mod115, new AsyncCallbackWrapper<Mod115>(callback));
	}
	
	@Override
	public void markAsPending(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.markAsPending(occam, mod115, new AsyncCallbackWrapper<Mod115>(callback));
	}

	@Override
	public void markAsSent(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.markAsSent(occam, mod115, new AsyncCallbackWrapper<Mod115>(callback));
	}
	@Override
	public void markAsCustomerCheck(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.markAsCustomerCheck(occam, mod115, new AsyncCallbackWrapper<Mod115>(callback));
	}
	
	@Override
	public void initialize(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.initialize(occam, mod115,new AsyncCallbackWrapper<Mod115>(callback));

	}

	@Override
	public void create(Occam occam, Mod115 mod115, AsyncCallback<Mod115> callback) {
		AON.start();
		fsa.create(occam, mod115,new AsyncCallbackWrapper<Mod115>(callback));
	}

	@Override
	public void delete(Occam occam, Mod115 mod115, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod115, new AsyncCallbackWrapper<Void>(callback));
	}
	@Override
	public void getInfo(Occam occam, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod115, script, infoKey,new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<Invoice>(callback));
	}
	
}
