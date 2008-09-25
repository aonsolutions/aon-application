package com.code.aon.ui.finance.event;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PurchaseDirectInvoicingDetailControllerListener extends ControllerAdapter {
	
	private IPriceStrategy priceStrategy;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();  
		obtainTaxableBase(invoiceDetail);
		((InvoiceDetail)event.getController().getTo()).setSource(InvoiceSource.DIRECT_PURCHASE);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		obtainTaxableBase(invoiceDetail);
	}
	
	private void obtainTaxableBase(InvoiceDetail invoiceDetail) {
		invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
	}
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}
}