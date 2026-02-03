package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceCollectionInfo implements Serializable {
	
	private static final long serialVersionUID = -6061576110389791837L;
	
	private int totalCount;
	private double totalAmount;
	private double totalVAT;
	private double totalRetention;
	private int totalPrepaymentCount;
	
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
	
	public double getTotalVAT() {
		return totalVAT;
	}
	public InvoiceCollectionInfo setTotalVAT(double totalVAT) {
		this.totalVAT = totalVAT;
		return this;
	}
	
	public double getTotalRetention() {
		return totalRetention;
	}
	public InvoiceCollectionInfo setTotalRetention(double totalRetention) {
		this.totalRetention = totalRetention;
		return this;
	}
	
	public int getTotalPrepaymentCount() {
		return totalPrepaymentCount;
	}
	public InvoiceCollectionInfo setTotalPrepaymentCount(int totalPrepaymentCount) {
		this.totalPrepaymentCount = totalPrepaymentCount;
		return this;
	}
	
	public void addInvoice(Invoice invoice) {
		addTotalCount();
		addTotalAmount( invoice.getTotal() );
		addTotalVAT( invoice.getVatQuota() );
		addTotalRetention( invoice.getRetentionQuota() );
		addPrepaymentCount( invoice.hasPrepayments() );
	}
	
	private InvoiceCollectionInfo addTotalCount() {
		this.totalCount++;
		return this;
	}
	private InvoiceCollectionInfo addTotalAmount(double amount) {
		this.totalAmount = AonMathUtils.round( this.totalAmount + amount);
		return this;
	}
	private InvoiceCollectionInfo addTotalVAT(double vat) {
		this.totalVAT = AonMathUtils.round( this.totalVAT + vat);
		return this;
	}
	private InvoiceCollectionInfo addTotalRetention(double retention) {
		this.totalRetention = AonMathUtils.round( this.totalRetention + retention);
		return this;
	}
	private InvoiceCollectionInfo addPrepaymentCount(boolean hasPrepayments) {
		totalPrepaymentCount += hasPrepayments ? 1 : 0;
		return this;
	}
	
}
