package com.code.aon.ui.finance;

import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.Invoice;
import com.code.aon.registry.ITaxInfo;

public class InvoiceTaxInfo implements ITaxInfo {

	private Invoice invoice;
	
	public InvoiceTaxInfo(Invoice invoice) {
		this.invoice = invoice;
	}
	
	@Override
	public boolean isSurcharge() {
		return invoice.isSurcharge();
	}

	@Override
	public boolean isWithholding() {
		return invoice.isWithholding();
	}

	@Override
	public boolean isWithholdingFarmer() {
		return invoice.isWithholdingFarmer();
	}

	@Override
	public boolean isVatAccrualPayment() {
		return invoice.isVatAccrualPayment();
	}

	@Override
	public InvoiceTransactionType getTransaction() {
		return invoice.getTransaction();
	}

	@Override
	public boolean isVatFree() {
		return false;
	}

	@Override
	public boolean isRetentionFree() {
		return false;
	}

}
