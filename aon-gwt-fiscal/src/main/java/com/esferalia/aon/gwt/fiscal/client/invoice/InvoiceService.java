package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AonInvoiceService")
public interface InvoiceService extends RemoteService {

	// 										[CONFIGURATION]
	AonConfiguration getAonConfiguration(Occam occam) throws AonCoreException;
	
	//										[INVOICE]
	Invoice save(Occam occam, Invoice invoice) throws AonCoreException;
	Invoice delete(Occam occam, Integer invoiceId) throws AonCoreException;
}
