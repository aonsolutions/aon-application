package com.code.aon.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.InvoiceTax;

public class InvoiceTaxBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceTax invoiceTax = (InvoiceTax)evt.getTo();
		checkInvoiceTax(invoiceTax);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		InvoiceTax invoiceTax = (InvoiceTax)evt.getTo();
		checkInvoiceTax(invoiceTax);
	}

	private void checkInvoiceTax(InvoiceTax invoiceTax) {
		if (invoiceTax.getBase() == 0) {
			invoiceTax.setBase(invoiceTax.getInvoiceDetail().getTaxableBase());
		}

		if (invoiceTax.isVat()) {
			if (invoiceTax.getInvoiceDetail().getInvestAsset() != null && invoiceTax.getInvoiceDetail().getInvestAsset().getId() != null) {
				invoiceTax.setDeductiblePercent(invoiceTax.getInvoiceDetail().getInvestAsset().getVatPercent());
			} else {
				invoiceTax.setDeductiblePercent(100);
			}
			if (invoiceTax.getQuota() != 0) {
				invoiceTax.setDeductibleQuota(CommonUtil.round(invoiceTax.getQuota() * invoiceTax.getDeductiblePercent() / 100));
			}
		} else {
			invoiceTax.setDeductiblePercent(0);
			invoiceTax.setDeductibleQuota(0);
		}
	}

}
