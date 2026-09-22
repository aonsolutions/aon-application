package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceCollectionInfo implements Serializable {
	
	private static final long serialVersionUID = -6061576110389791837L;
	
	private int totalCount;
	private double totalAmount;
	
	public int getTotalCount() {
		return totalCount;
	}
	public InvoiceCollectionInfo setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		return this;
	}
	
	public double getTotalAmount() {
		return totalAmount;
	}
	public InvoiceCollectionInfo setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}
	
	public void addInvoice(Invoice invoice) {
		addTotalCount();
		addTotalAmount( invoice.getTotal() );
	}
	
	private InvoiceCollectionInfo addTotalCount() {
		this.totalCount++;
		return this;
	}
	private InvoiceCollectionInfo addTotalAmount(double amount) {
		this.totalAmount = AonMathUtils.round( this.totalAmount + amount);
		return this;
	}
	
}
