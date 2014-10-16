package com.code.aon.ui.sales.controller;

import static com.code.aon.common.IProgression.FINISH_VALUE;

import com.code.aon.common.IProgression;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.bridge.invoicing.SalesInvoicingManager;
import com.code.aon.sales.Sales;
import com.code.aon.ui.common.ILongProcess;

public class SalesInvoiceProcess implements ILongProcess {

	private SalesController salesController;
	
	public SalesInvoiceProcess(SalesController salesController) {
		this.salesController = salesController;
	}
	
	@Override
	public void execute() {
		salesController.setInvoiceId(null);
		try {
			Sales to = (Sales) salesController.getTo();
			SalesInvoicingManager invoicingManager = new SalesInvoicingManager();
			invoicingManager.setProgression(salesController.getProgressionState());
			Invoice invoice = invoicingManager.invoice(to, salesController.getInvoiceSeries(), salesController.getInvoiceNumber(), salesController.getInvoiceDate());
			salesController.setInvoiceId(invoice.getId());
			salesController.getProgressionState().setProgressionCurrentValue(FINISH_VALUE);
		} catch (Throwable e) {
			String msg = "No se pudo grabar la factura. (" + e.getMessage()+ ")";
			salesController.getProgressionState().setProgressionErrorMessage(msg);
			salesController.getProgressionState().setProgressionCurrentValue(IProgression.ERROR_VALUE);
		}
	}

}
