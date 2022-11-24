package com.esferalia.aon.gwt.fiscal.client.mod390hf;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Mod390HFServiceAsyncDecorator implements Mod390HFServiceAsync {

	private Mod390HFServiceAsync fsa;

	public Mod390HFServiceAsyncDecorator(Mod390HFServiceAsync mod190ServiceAsync) {
		this.fsa = mod190ServiceAsync;
	}

	// ---------------------------------------------------------------MODELO 390HF

	@Override
	public void get(Occam occam, int id, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.get(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod390HFs(Occam occam, AsyncCallback<LinkedList<Mod390HF>> callback) {
		AON.start();
		fsa.getMod390HFs(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void calculate(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.calculate(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void calculateProrrate(Occam occam, Mod390HF model, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.calculate(occam, model, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.save(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveComments(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.saveComments(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initializeForFinish(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.initializeForFinish(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.initialize(occam, mod390HF, new AsyncCallbackWrapper<>(callback));

	}

	@Override
	public void create(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.create(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void reset(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.reset(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void delete(Occam occam, Mod390HF mod390HF, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getInfo(Occam occam, Mod390HF mod390HF, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod390HF, script, infoKey, new AsyncCallbackWrapper<>(callback));

	}

	@Override
	public void markAsFinished(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsFinished(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsPending(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsPending(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsSent(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsSent(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsCustomerCheck(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsCustomerCheck(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsCustomerAccepted(Occam occam, Mod390HF mod390HF, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsCustomerAccepted(occam, mod390HF, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void markAsCustomerRejected(Occam occam, Mod390HF mod390HF, String reason, AsyncCallback<Mod390HF> callback) {
		AON.start();
		fsa.markAsCustomerRejected(occam, mod390HF, reason, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
}
