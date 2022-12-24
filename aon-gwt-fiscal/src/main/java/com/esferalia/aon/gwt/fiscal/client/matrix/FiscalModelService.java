package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceFiscalModels;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("roms/FiscalModels")
public interface FiscalModelService extends RemoteService {

//	LinkedList<IFiscalModel> getFiscalPanel(String domainName,String user,int domain, FiscalMatrixParams params);
	
	LinkedList<InvoiceFiscalModels> getInvoicesModels(Occam occam, InvoiceModelReportParams params);
	Invoice getInvoice(Occam occam, Integer invoiceId);
	
}
