package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetailFlat;
import com.esferalia.aon.occam.api.model.accounting.AmortizationInvoice;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AmortizationServiceAsync {

	void get(Occam occam, Integer domain, Integer id, AsyncCallback<Amortization> callback);
	void get(Occam occam, AmortizationParams params, AsyncCallback<LinkedList<Amortization>> callback);
	void getFlat(Occam occam, AmortizationParams params, AsyncCallback<LinkedList<AmortizationDetailFlat>> callback);
	void save(Occam occam, Amortization am, AsyncCallback<Amortization> callback);
	void saveFiscalAllocation (Occam occam, AmortizationDetail detail, AsyncCallback<Amortization> callback);
	void delete(Occam occam, Amortization am, AsyncCallback<Void> callback);
	void sale(Occam occam, Amortization am, AsyncCallback<Amortization> callback);
	void calculate(Occam occam, Amortization am, AsyncCallback<Amortization> callback);
	
	void recordAllocation(Occam occam, Amortization am, AmortizationDetail detail, AsyncCallback<AmortizationDetail> callback);
	void unrecordAllocation(Occam occam, AmortizationDetail detail, AsyncCallback<AmortizationDetail> callback);
	
	void blockDetail(Occam occam, AmortizationDetail detail, AsyncCallback<AmortizationDetail> callback);
	void unblockDetail(Occam occam, AmortizationDetail detail, AsyncCallback<AmortizationDetail> callback);

	void getInvoices(Occam occam, Integer domain, Integer amortizationId, AsyncCallback<LinkedList<AmortizationInvoice>> callback);
	void linkInvoices(Occam occam, Integer domain, Integer amortizationId, Integer[] invoiceIds, AsyncCallback<Void> asyncCallback);
	void unlinkInvoice(Occam occam, Integer domain, Integer amortizationId, Integer invoiceId, AsyncCallback<Void> asyncCallback);
	void changeInvestment(Occam occam, Integer domain, Integer invoiceId, AsyncCallback<Invoice> asyncCallback);
}
