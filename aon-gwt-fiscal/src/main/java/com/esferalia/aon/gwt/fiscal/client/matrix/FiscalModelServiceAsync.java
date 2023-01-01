package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceFiscalModels;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalModelServiceAsync {

//	void getFiscalPanel(String domainName,String user,int domain,FiscalMatrixParams params, AsyncCallback<LinkedList<IFiscalModel>> asyncCallback);
	void getInvoicesModels(Occam occam, InvoiceModelReportParams params, AsyncCallback<LinkedList<InvoiceFiscalModels>> callback);
	void getInvoice(Occam occam, Integer invoiceId, AsyncCallback<Invoice> callback);

}
