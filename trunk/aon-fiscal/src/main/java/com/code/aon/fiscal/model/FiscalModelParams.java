package com.code.aon.fiscal.model;

import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.fiscal.FiscalModel;

public class FiscalModelParams {
	
	private FiscalModel fiscalModel;
	private InvoiceStatus invoiceStatus;

	public FiscalModel getFiscalModel() {
		return fiscalModel;
	}
	public void setFiscalModel(FiscalModel fiscalModel) {
		this.fiscalModel = fiscalModel;
	}
	
	public InvoiceStatus getInvoiceStatus() {
		return invoiceStatus;
	}
	public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

}
