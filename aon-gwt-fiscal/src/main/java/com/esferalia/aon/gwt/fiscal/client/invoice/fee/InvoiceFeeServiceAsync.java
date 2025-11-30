package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InvoiceFeeServiceAsync {

	
	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	void getAonConfiguration(Occam occam, AsyncCallback<AonConfiguration> callback) throws AonCoreException;
	
	// **************************************************
	// ******************************************** [FEE]
	// **************************************************
	void getFeeYearRange(Occam occam, Integer domainId, AsyncCallback<Pair<Integer, Integer>> callback) throws AonCoreException;

	// **************************************************
	// ************************************** [INVOICING]
	// **************************************************
	void getInvoices(Occam occam, FeeBillingParams params, AsyncCallback<InvoiceProcessOutput> callback) throws AonCoreException;
	void saveInvoice(Occam occam, Invoice invoice, AsyncCallback<Invoice> callback) throws AonCoreException;
}
