package com.code.aon.gta.print;

import com.code.aon.common.ITransferObject;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.SupportOrderInsurance;
import com.code.aon.warehouse.Delivery;

public class ReportInvoiceDetail implements ITransferObject {

	private InvoiceDetail invoiceDetail;
	
	private Delivery delivery;
	
	private SupportOrder supportOrder;
	
	private SupportOrderInsurance supportOrderInsurance;
	
	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}

	public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public SupportOrder getSupportOrder() {
		return supportOrder;
	}

	public void setSupportOrder(SupportOrder supportOrder) {
		this.supportOrder = supportOrder;
	}

	public SupportOrderInsurance getSupportOrderInsurance() {
		return supportOrderInsurance;
	}

	public void setSupportOrderInsurance(SupportOrderInsurance supportOrderInsurance) {
		this.supportOrderInsurance = supportOrderInsurance;
	}

}