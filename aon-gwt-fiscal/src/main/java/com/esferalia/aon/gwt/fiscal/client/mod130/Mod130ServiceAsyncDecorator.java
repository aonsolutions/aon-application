package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
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
	public void getMod130(Occam occam, int id, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.getMod130(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod130s(Occam occam, AsyncCallback<LinkedList<Mod130>> callback) {
		AON.start();
		fsa.getMod130s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void calculate(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.calculate(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.save(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.saveComments(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initializeForFinish(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.initializeForFinish(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void initialize(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.initialize(occam,mod130,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void create(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.create(occam, mod130,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod130 mod130, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getInfo(Occam occam, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod130, script, infoKey,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsFinished(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.markAsFinished(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsPending(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.markAsPending(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsSent(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.markAsSent(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsCustomerCheck(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.markAsCustomerCheck(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsCustomerAccepted(Occam occam, Mod130 mod130, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.markAsCustomerAccepted(occam, mod130, new AsyncCallbackWrapper<>(callback));
	}

	
	@Override
	public void markAsCustomerRejected(Occam occam, Mod130 mod130, String reason, AsyncCallback<Mod130> callback) {
		AON.start();
		fsa.markAsCustomerRejected(occam, mod130, reason, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
}
