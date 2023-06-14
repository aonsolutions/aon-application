package net.aonsolutions.infovox.model;

import java.util.Optional;

public class OCRInvoiceLine {
	
	private OCRString group = null;
	private OCRString identifier = null;
	private OCRString description = null;
	private OCRString date = null;
	private OCRNumber quantity = null;
	private OCRNumber unitOfMeasurement = null;
	private OCRNumber grossUnitPrice = null;
	private OCRNumber taxBaseUnitPrice = null;
	private OCRNumber totalUnitPrice = null;
	private OCRNumber discountAmount = null;
	private OCRNumber discountRate = null;
	private OCRNumber taxRate = null;
	private OCRNumber taxAmount = null;
	private OCRNumber taxBaseAmount = null;
	private OCRNumber totalAmount = null;
	private OCRNumber grossAmount = null;
	private OCRNumber feesAmount = null;
	private OCRNumber feesRate = null;
	
	public Optional<OCRString> getGroup() {
		return Optional.ofNullable(group);
	}
	public OCRInvoiceLine setGroup(OCRString group) {
		this.group = group;
		return this;
	}
	
	public Optional<OCRString> getIdentifier() {
		return Optional.ofNullable(identifier);
	}
	public OCRInvoiceLine setIdentifier(OCRString identifier) {
		this.identifier = identifier;
		return this;
	}
	
	public Optional<OCRString> getDescription() {
		return Optional.ofNullable(description);
	}
	public OCRInvoiceLine setDescription(OCRString description) {
		this.description = description;
		return this;
	}
	
	public Optional<OCRString> getDate() {
		return Optional.ofNullable(date);
	}
	public OCRInvoiceLine setDate(OCRString date) {
		this.date = date;
		return this;
	}
	
	public Optional<OCRNumber> getQuantity() {
		return Optional.ofNullable(quantity);
	}
	public OCRInvoiceLine setQuantity(OCRNumber quantity) {
		this.quantity = quantity;
		return this;
	}
	
	public Optional<OCRNumber> getUnitOfMeasurement() {
		return Optional.ofNullable(unitOfMeasurement);
	}
	public OCRInvoiceLine setUnitOfMeasurement(OCRNumber unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
		return this;
	}
	
	public Optional<OCRNumber> getGrossUnitPrice() {
		return Optional.ofNullable(grossUnitPrice);
	}
	public OCRInvoiceLine setGrossUnitPrice(OCRNumber grossUnitPrice) {
		this.grossUnitPrice = grossUnitPrice;
		return this;
	}
	
	public Optional<OCRNumber> getTaxBaseUnitPrice() {
		return Optional.ofNullable(taxBaseUnitPrice);
	}
	public OCRInvoiceLine setTaxBaseUnitPrice(OCRNumber taxBaseUnitPrice) {
		this.taxBaseUnitPrice = taxBaseUnitPrice;
		return this;
	}
	
	public Optional<OCRNumber> getTotalUnitPrice() {
		return Optional.ofNullable(totalUnitPrice);
	}
	public OCRInvoiceLine setTotalUnitPrice(OCRNumber totalUnitPrice) {
		this.totalUnitPrice = totalUnitPrice;
		return this;
	}
	
	public Optional<OCRNumber> getDiscountAmount() {
		return Optional.ofNullable(discountAmount);
	}
	public OCRInvoiceLine setDiscountAmount(OCRNumber discountAmount) {
		this.discountAmount = discountAmount;
		return this;
	}
	
	public Optional<OCRNumber> getDiscountRate() {
		return Optional.ofNullable(discountRate);
	}
	public OCRInvoiceLine setDiscountRate(OCRNumber discountRate) {
		this.discountRate = discountRate;
		return this;
	}
	
	public Optional<OCRNumber> getTaxRate() {
		return Optional.ofNullable(taxRate);
	}
	public OCRInvoiceLine setTaxRate(OCRNumber taxRate) {
		this.taxRate = taxRate;
		return this;
	}
	
	public Optional<OCRNumber> getTaxAmount() {
		return Optional.ofNullable(taxAmount);
	}
	public OCRInvoiceLine setTaxAmount(OCRNumber taxAmount) {
		this.taxAmount = taxAmount;
		return this;
	}
	
	public Optional<OCRNumber> getTaxBaseAmount() {
		return Optional.ofNullable(taxBaseAmount);
	}
	public OCRInvoiceLine setTaxBaseAmount(OCRNumber taxBaseAmount) {
		this.taxBaseAmount = taxBaseAmount;
		return this;
	}
	
	public Optional<OCRNumber> getTotalAmount() {
		return Optional.ofNullable(totalAmount);
	}
	public OCRInvoiceLine setTotalAmount(OCRNumber totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}
	
	public Optional<OCRNumber> getGrossAmount() {
		return Optional.ofNullable(grossAmount);
	}
	public OCRInvoiceLine setGrossAmount(OCRNumber grossAmount) {
		this.grossAmount = grossAmount;
		return this;
	}
	
	public Optional<OCRNumber> getFeesAmount() {
		return Optional.ofNullable(feesAmount);
	}
	public OCRInvoiceLine setFeesAmount(OCRNumber feesAmount) {
		this.feesAmount = feesAmount;
		return this;
	}
	
	public Optional<OCRNumber> getFeesRate() {
		return Optional.ofNullable(feesRate);
	}
	public OCRInvoiceLine setFeesRate(OCRNumber feesRate) {
		this.feesRate = feesRate;
		return this;
	}
	
}
