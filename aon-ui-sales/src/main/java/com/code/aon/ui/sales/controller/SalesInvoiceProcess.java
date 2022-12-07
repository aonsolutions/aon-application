package com.code.aon.ui.sales.controller;

import static com.code.aon.common.IProgression.FINISH_VALUE;

import com.code.aon.common.IProgression;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.bridge.invoicing.SalesInvoicingManager;
import com.code.aon.sales.Sales;
import com.code.aon.ui.common.ILongProcess;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;

public class SalesInvoiceProcess implements ILongProcess {

	private SalesController salesController;
	
	public SalesInvoiceProcess(SalesController salesController) {
		this.salesController = salesController;
	}

	@Override
	public void execute() {
		Sales to = (Sales)salesController.getTo();
		try {
			boolean tbai = salesController.isTbai();
		    if(tbai) {
		        String domainName = AonUtil.getDomainName();
				Integer domainId = DomainManager.getCurrentDomain();
				Integer number = AON.getInvoiceMinNumber(domainName, domainId, "", com.esferalia.aon.occam.api.model.type.InvoiceType.SALES, salesController.getInvoiceSeries());
				if(number >= 0) number = -1; 
				salesController.setInvoiceNumber(number);
	        }
			SalesInvoicingManager invoicingManager = new SalesInvoicingManager();
			invoicingManager.setProgression(salesController.getProgressionState());
			Invoice invoice = invoicingManager.invoice(to, salesController.getInvoiceSeries(), salesController.getInvoiceNumber(), salesController.getInvoiceDate(), tbai);
			salesController.setInvoiceId(invoice.getId());
			salesController.getProgressionState().setProgressionCurrentValue(FINISH_VALUE);
		} catch (Throwable ex) {
			salesController.setInvoiceId(null);
			salesController.getProgressionState().setProgressionErrorMessage(ex.getMessage());
			salesController.getProgressionState().setProgressionCurrentValue(IProgression.ERROR_VALUE);
		}
	}

}
