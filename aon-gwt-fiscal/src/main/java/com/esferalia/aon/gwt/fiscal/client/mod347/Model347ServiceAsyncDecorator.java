package com.esferalia.aon.gwt.fiscal.client.mod347;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model347ServiceAsyncDecorator implements Model347ServiceAsync {
	
	private Model347ServiceAsync fsa;

	public Model347ServiceAsyncDecorator(Model347ServiceAsync mod347ServiceAsync) {
		this.fsa = mod347ServiceAsync;
	}
	
	@Override
	public void getMod347s(Occam occam, AsyncCallback<LinkedList<Mod347>> callback) {
		AON.start();
		fsa.getMod347s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void get(Occam occam, Integer id, AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.get(occam, id, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void initialize(Occam occam, int year, AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.initialize(occam, year, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void reset(Occam occam, Mod347 model, AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.reset(occam, model, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void save(Occam occam, Mod347 mod347,AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.save(occam, mod347, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod347 mod347, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod347,new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod347 mod347, AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.saveComments(occam,mod347, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void changeStatus(Occam occam,Mod347 mod347, FiscalStatus newStatus, AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.changeStatus(occam,mod347, newStatus, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInfo(Occam occam, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod347, declared, infoKey, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void duplicate(Occam occam, Mod347 mod347, AsyncCallback<Mod347> callback) {
		AON.start();
		fsa.duplicate(occam, mod347, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}

}
