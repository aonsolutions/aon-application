package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IrpfReportServiceAsyncDecorator implements IrpfReportServiceAsync {

	private IrpfReportServiceAsync fsa;

	public IrpfReportServiceAsyncDecorator(IrpfReportServiceAsync serviceAsync) {
		this.fsa = serviceAsync;
	}

	@Override
	public void getIrpfBreakdownSummary(Occam occam, IRPFParams params, AsyncCallback<LinkedList<IrpfBreakdown>> callback) {
		AON.start();
		fsa.getIrpfBreakdownSummary(occam, params, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getIrpfBreakdown(Occam occam, IRPFParams params, AsyncCallback<LinkedList<IrpfBreakdown>> callback) {
		AON.start();
		fsa.getIrpfBreakdown(occam, params, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getInvoice(Occam occam, int invoiceId, AsyncCallback<Invoice> callback) {
		AON.start();
		fsa.getInvoice(occam, invoiceId, new AsyncCallbackWrapper<>(callback));
	}
}
