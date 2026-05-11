package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.accounting.AmortizationInvoice;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AmortizationServiceAsyncDecorator implements AmortizationServiceAsync {

	private AmortizationServiceAsync serviceAsync;

	public AmortizationServiceAsyncDecorator(AmortizationServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	@Override
	public void get(Occam occam, Integer domain, Integer id, AsyncCallback<Amortization> callback) {
		AON.start();
		serviceAsync.get(occam, domain, id, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void get(Occam occam, AmortizationParams params, AsyncCallback<LinkedList<Amortization>> callback) {
		AON.start();
		serviceAsync.get(occam, params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void saveFiscalAllocation(Occam occam, AmortizationDetail detail, AsyncCallback<Amortization> callback) {
		AON.start();
		serviceAsync.saveFiscalAllocation(occam, detail, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void save(Occam occam, Amortization am, AsyncCallback<Amortization> callback) {
		AON.start();
		serviceAsync.save(occam, am, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void delete(Occam occam, Amortization am, AsyncCallback<Void> callback) {
		AON.start();
		serviceAsync.delete(occam, am, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void calculate(Occam occam, Amortization am, AsyncCallback<Amortization> callback) {
		AON.start();
		serviceAsync.calculate(occam, am, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void sale(Occam occam, Amortization am, AsyncCallback<Amortization> callback) {
		AON.start();
		serviceAsync.sale(occam, am, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void recordAllocation(Occam occam, Amortization am, AmortizationDetail detail, AsyncCallback<AmortizationDetail> callback) {
		AON.start();
		serviceAsync.recordAllocation(occam, am, detail, new AsyncCallbackWrapper<>(callback ));
	}
	
	@Override
	public void unrecordAllocation(Occam occam, AmortizationDetail detail, AsyncCallback<AmortizationDetail> callback) {
		AON.start();
		serviceAsync.unrecordAllocation(occam, detail, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void blockDetail(Occam occam, AmortizationDetail detail, AsyncCallback<AmortizationDetail> callback) {
		AON.start();
		serviceAsync.blockDetail(occam, detail, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void unblockDetail(Occam occam, AmortizationDetail detail, AsyncCallback<AmortizationDetail> callback) {
		AON.start();
		serviceAsync.unblockDetail(occam, detail, new AsyncCallbackWrapper<>(callback));
	}
	

	@Override
	public void getInvoices(Occam occam, Integer domain, Integer amortizationId, AsyncCallback<LinkedList<AmortizationInvoice>> callback) {
		AON.start();
		serviceAsync.getInvoices(occam, domain, amortizationId, new AsyncCallbackWrapper<>(callback));
	}
				
	@Override
	public void linkInvoices(Occam occam, Integer domain, Integer amortizationId, Integer[] invoiceUds, AsyncCallback<Void> asyncCallback) {
		AON.start();
		serviceAsync.linkInvoices(occam, domain, amortizationId, invoiceUds, new AsyncCallbackWrapper<>(asyncCallback));
	}
	
	@Override
	public void unlinkInvoice(Occam occam, Integer domain, Integer amortizationId, Integer invoiceId, AsyncCallback<Void> asyncCallback) {
		AON.start();
		serviceAsync.unlinkInvoice(occam, domain, amortizationId, invoiceId, new AsyncCallbackWrapper<>(asyncCallback));
	}
}
