package com.code.aon.account.bridge.event;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.accounting.AmortizationInvoice;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
			if (invoice.isInvestment()) {
				IManagerBean amortizationInvoiceBean = BeanManager.getManagerBean(AmortizationInvoice.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(amortizationInvoiceBean.getFieldName(IEntityAlias.AMORTIZATION_INVOICE_INVOICE_ID), invoice.getId());
				if (amortizationInvoiceBean.getCount(criteria) > 0) {
					throw new ManagerBeanVetoListenerException("La Factura " + invoice.getReferenceCode() + " no se puede borrar. " +
																 "Tiene asociada una ficha de Amortización.");
				}
			}
			if (invoice.getStatus().equals(InvoiceStatus.SCORED)) {
				getAccountEntryInvoiceWriter().unrecordInvoice(invoice);
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e);
		}
	}
}