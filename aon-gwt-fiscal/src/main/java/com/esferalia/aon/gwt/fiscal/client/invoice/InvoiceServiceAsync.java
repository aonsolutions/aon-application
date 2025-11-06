package com.esferalia.aon.gwt.fiscal.client.invoice;


import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InvoiceServiceAsync {

	// 										[CONFIGURATION]
	void getAonConfiguration(Occam occam, AsyncCallback<AonConfiguration> callback) throws AonCoreException;
	
	//										[INVOICE]
	void save(Occam occam, Invoice invoice, AsyncCallback<Invoice> callback) throws AonCoreException;
	void delete(Occam occam, Integer invoiceId, AsyncCallback<Invoice> callback) throws AonCoreException;
	
	
}
