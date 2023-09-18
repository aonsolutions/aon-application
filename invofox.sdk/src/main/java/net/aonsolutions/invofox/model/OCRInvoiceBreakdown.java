package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRInvoiceBreakdown implements Serializable {

	private static final long serialVersionUID = 990116708551305340L;
	
	private OCRNumber taxRate = null;
	private OCRNumber taxBaseAmount = null;
	private OCRNumber taxAmount = null;
	private OCRNumber reRate = null;
	private OCRNumber reAmount = null;
	private OCRNumber totalAmount = null;
	private OCRNumber grossAmount = null;
	private OCRNumber discountBaseAmount = null;
	private OCRNumber discountAmount = null;
	private OCRNumber discountRate = null;
	private OCRNumber feesBaseAmount = null;
	private OCRNumber feesAmount = null;
	private OCRNumber feesRate = null;
	
	public Optional<OCRNumber> getTaxRate() {
		return Optional.ofNullable(taxRate);
	}
	public OCRInvoiceBreakdown setTaxRate(OCRNumber taxRate) {
		this.taxRate = taxRate;
		return this;
	}
	
	public Optional<OCRNumber> getTaxBaseAmount() {
		return Optional.ofNullable(taxBaseAmount);
	}
	public OCRInvoiceBreakdown setTaxBaseAmount(OCRNumber taxBaseAmount) {
		this.taxBaseAmount = taxBaseAmount;
		return this;
	}
	
	public Optional<OCRNumber> getTaxAmount() {
		return Optional.ofNullable(taxAmount);
	}
	public OCRInvoiceBreakdown setTaxAmount(OCRNumber taxAmount) {
		this.taxAmount = taxAmount;
		return this;
	}
	
	public Optional<OCRNumber> getReRate() {
		return Optional.ofNullable(reRate);
	}
	public OCRInvoiceBreakdown setReRate(OCRNumber reRate) {
		this.reRate = reRate;
		return this;
	}
	
	public Optional<OCRNumber> getReAmount() {
		return Optional.ofNullable(reAmount);
	}
	public OCRInvoiceBreakdown setReAmount(OCRNumber reAmount) {
		this.reAmount = reAmount;
		return this;
	}
	
	public Optional<OCRNumber> getTotalAmount() {
		return Optional.ofNullable(totalAmount);
	}
	public OCRInvoiceBreakdown setTotalAmount(OCRNumber totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}
	
	public Optional<OCRNumber> getGrossAmount() {
		return Optional.ofNullable(grossAmount);
	}
	public OCRInvoiceBreakdown setGrossAmount(OCRNumber grossAmount) {
		this.grossAmount = grossAmount;
		return this;
	}
	
	public Optional<OCRNumber> getDiscountBaseAmount() {
		return Optional.ofNullable(discountBaseAmount);
	}
	public OCRInvoiceBreakdown setDiscountBaseAmount(OCRNumber discountBaseAmount) {
		this.discountBaseAmount = discountBaseAmount;
		return this;
	}
	
	public Optional<OCRNumber> getDiscountAmount() {
		return Optional.ofNullable(discountAmount);
	}
	public OCRInvoiceBreakdown setDiscountAmount(OCRNumber discountAmount) {
		this.discountAmount = discountAmount;
		return this;
	}
	
	public Optional<OCRNumber> getDiscountRate() {
		return Optional.ofNullable(discountRate);
	}
	public OCRInvoiceBreakdown setDiscountRate(OCRNumber discountRate) {
		this.discountRate = discountRate;
		return this;
	}
	
	public Optional<OCRNumber> getFeesBaseAmount() {
		return Optional.ofNullable(feesBaseAmount);
	}
	public OCRInvoiceBreakdown setFeesBaseAmount(OCRNumber feesBaseAmount) {
		this.feesBaseAmount = feesBaseAmount;
		return this;
	}
	
	public Optional<OCRNumber> getFeesAmount() {
		return Optional.ofNullable(feesAmount);
	}
	public OCRInvoiceBreakdown setFeesAmount(OCRNumber feesAmount) {
		this.feesAmount = feesAmount;
		return this;
	}
	
	public Optional<OCRNumber> getFeesRate() {
		return Optional.ofNullable(feesRate);
	}
	public OCRInvoiceBreakdown setFeesRate(OCRNumber feesRate) {
		this.feesRate = feesRate;
		return this;
	}

}