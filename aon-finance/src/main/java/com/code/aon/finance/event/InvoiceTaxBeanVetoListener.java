package com.code.aon.finance.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.InvoiceTax;

public class InvoiceTaxBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceTax invoiceTax = (InvoiceTax)evt.getTo();
		if (invoiceTax.getBase() == 0) {
			invoiceTax.setBase(invoiceTax.getInvoiceDetail().getTaxableBase());
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceTax invoiceTax = (InvoiceTax)evt.getTo();
		if (invoiceTax.getBase() == 0) {
			invoiceTax.setBase(invoiceTax.getInvoiceDetail().getTaxableBase());
		}
	}

}
