package com.code.aon.account.bridge.event;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;

public class InvoiceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if (accountEntryInvoiceWriter == null) {
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			Invoice invoice = (Invoice) evt.getTo();
			if (invoice.getStatus().equals(InvoiceStatus.SCORED)) {
				getAccountEntryInvoiceWriter().unrecordInvoice(invoice);
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e);
			
		}
	}
}