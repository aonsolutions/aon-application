package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
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
	public void get(Occam occam, int id, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.get(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod131s(Occam occam, AsyncCallback<LinkedList<Mod131>> callback) {
		AON.start();
		fsa.getMod131s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void calculate(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.calculate(occam, mod131, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void calculateActivity(Occam occam, Mod131 mod131,Mod131Activity activity, AsyncCallback<Mod131Activity> callback) {
		AON.start();
		fsa.calculateActivity(occam, mod131, activity, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.save(occam, mod131, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.saveComments(occam, mod131, new AsyncCallbackWrapper<>( callback));
	}

	@Override
	public void initializeForFinish(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.initializeForFinish(occam, mod131, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsFinished(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.markAsFinished(occam, mod131, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void markAsSent(Occam occam, Mod131 mod131,AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.markAsSent(occam, mod131, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void markAsCustomerCheck(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.markAsCustomerCheck(occam, mod131, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void markAsPending(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.markAsPending(occam, mod131, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.initialize(occam, mod131,new AsyncCallbackWrapper<>(callback));

	}

	@Override
	public void create(Occam occam, Mod131 mod131, AsyncCallback<Mod131> callback) {
		AON.start();
		fsa.create(occam, mod131,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod131 mod131, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod131, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInfo(Occam occam, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod131, script, infoKey,new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
}
