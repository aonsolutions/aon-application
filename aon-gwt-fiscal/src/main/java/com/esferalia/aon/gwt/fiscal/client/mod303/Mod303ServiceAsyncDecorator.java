package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod303ServiceAsyncDecorator implements Mod303ServiceAsync {

	private Mod303ServiceAsync fsa;

	public Mod303ServiceAsyncDecorator(Mod303ServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// ---------------------------------------------------------------MODELO 303

	@Override
	public void getMod303(Occam occam, int id, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.getMod303(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod303s(Occam occam,AsyncCallback<LinkedList<Mod303>> callback) {
		AON.start();
		fsa.getMod303s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.initialize(occam,mod303,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void create(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.create(occam, mod303,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void reset(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.reset(occam, mod303,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.save(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.saveComments(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void calculate(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.calculate(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void calculateProrrate(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.calculateProrrate(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initializeForFinish(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.initializeForFinish(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void delete(Occam occam, Mod303 mod303, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInfo(Occam occam, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod303, script, infoKey,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsPending(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.markAsPending(occam, mod303, new AsyncCallbackWrapper<>( callback));
	}

	@Override
	public void markAsFinished(Occam occam, Mod303 mod303,AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.markAsFinished(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsSent(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.markAsSent(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsCustomerCheck(Occam occam, Mod303 mod303,AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.markAsCustomerCheck(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}


	@Override
	public void doRecord(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.doRecord(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void unrecord(Occam occam, Mod303 mod303, AsyncCallback<Mod303> callback) {
		AON.start();
		fsa.unrecord(occam, mod303, new AsyncCallbackWrapper<>(callback));
	}
}
