package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.IrpfSummary;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IrpfReportServiceAsync {

	void getIrpfBreakdownSummary(Occam occam, IRPFParams params, AsyncCallback<IrpfSummary> callback);
	void getIrpfBreakdown(Occam occam, IRPFParams params, AsyncCallback<LinkedList<IrpfBreakdown>> callback);
	void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback);

}
