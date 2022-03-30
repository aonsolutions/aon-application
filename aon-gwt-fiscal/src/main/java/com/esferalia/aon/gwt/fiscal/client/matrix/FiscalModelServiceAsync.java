package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.InvoiceModelReportParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FiscalModelServiceAsync {

	void getFiscalPanel(String domainName,String user,int domain,FiscalMatrixParams params, AsyncCallback<LinkedList<IFiscalModel>> asyncCallback);
	void getInvoicesModels(Occam occam, InvoiceModelReportParams params, AsyncCallback<HashMap<Integer, LinkedList<FiscalModel>>> callback);
	void getInvoice(Occam occam, Integer invoiceId, AsyncCallback<Invoice> callback);

}
