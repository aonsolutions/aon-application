package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/InvoiceFee")
public interface InvoiceFeeService extends RemoteService {
	
	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	AonConfiguration getAonConfiguration(Occam occam) throws AonCoreException;
	
	// **************************************************
	// ******************************************** [FEE]
	// **************************************************
	Pair<Integer, Integer> getFeeYearRange(Occam occam, Integer domainId) throws AonCoreException;

	// **************************************************
	// ************************************** [INVOICING]
	// **************************************************
	InvoiceProcessOutput getInvoices(Occam occam, FeeBillingParams params) throws AonCoreException;	
	Invoice saveInvoice(Occam occam, Invoice invoice) throws AonCoreException;
}
