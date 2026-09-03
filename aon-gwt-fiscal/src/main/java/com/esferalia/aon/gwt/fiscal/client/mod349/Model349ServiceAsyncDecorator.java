package com.esferalia.aon.gwt.fiscal.client.mod349;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Model349ServiceAsyncDecorator implements Model349ServiceAsync {
	
	private Model349ServiceAsync fsa;

	public Model349ServiceAsyncDecorator(Model349ServiceAsync mod349ServiceAsync) {
		this.fsa = mod349ServiceAsync;
	}
	
	@Override
	public void delete(Occam occam, Mod349 mod349, AsyncCallback<Void> callback) {
		AON.start();
		fsa.delete(occam, mod349, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void save(Occam occam, Mod349 mod349, AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.save(occam, mod349,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void reset(Occam occam, Mod349 mod349, AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.reset(occam, mod349,new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getMod349s(Occam occam, AsyncCallback<LinkedList<Mod349>> callback) {
		AON.start();
		fsa.getMod349s(occam, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void initialize(Occam occam, int year, Period period, AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.initialize(occam, year, period, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void get(Occam occam, Integer id, AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.get(occam, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getDetail(Occam occam, Integer id, AsyncCallback<Mod349Detail> callback) {
		AON.start();
		fsa.getDetail(occam, id, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void saveComments(Occam occam, Mod349 mod349, AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.saveComments(occam, mod349, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void changeStatus(Occam occam, Mod349 mod349, FiscalStatus newStatus, AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.changeStatus(occam, mod349, newStatus, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInfo(Occam occam, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey, AsyncCallback<String> callback) {
		AON.start();
		fsa.getInfo(occam, mod349, detail, infoKey, new AsyncCallbackWrapper<>(callback));		
	}
	
	@Override
	public void duplicate(Occam occam, Mod349 mod349, AsyncCallback<Mod349> callback) {
		AON.start();
		fsa.duplicate(occam, mod349,new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}

}
