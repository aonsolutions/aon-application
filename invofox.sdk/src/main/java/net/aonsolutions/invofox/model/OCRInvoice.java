package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class OCRInvoice implements Serializable {
	
	private static final long serialVersionUID = 6531390994637826794L;
	
	private OCRString currency;
	private OCRString language;
	private OCRBoolean isCreditNote;
	private OCRString numberFormat;
	private OCRString invoiceRef;
	private OCRString seriesCode;
	private OCRString taxClass;
	private OCRString issuerCountry;
	private OCRString recipientCountry;
	private OCRAddress issuerAddressDetails;
	private OCRAddress recipientAddressDetails;
	private OCRString documentNumber;
	private OCRString issueDate;
	private OCRString issuerName;
	private OCRString issuerTaxId;
	private OCRString issuerAddress;
	private OCRString issuerEmail;
	private OCRString issuerPhoneNumber;
	private OCRString issuerWebsite;
	private OCRString recipientName;
	private OCRString recipientTaxId;
	private OCRString recipientAddress;
	private OCRString shippingAddress;
	private OCRString recipientEmail;
	private OCRString recipientWebsite;
	private OCRString recipientPhoneNumber;
	private OCRString paymentMethod;
	private OCRString IBAN;
	private OCRString SWIFT;
	private OCRString clientCode;
	private OCRString deliveryNoteRef;
	private OCRString orderRef;
	private OCRString contractRef;
	private OCRString incoterms;
	private OCRString documentType;
	private OCRString additionalNotes;
	private OCRString legalNotes;
	private OCRNumber totalTaxAmount;
	private OCRNumber totalTaxBaseAmount;
	private OCRNumber totalAmount;
	private OCRNumber totalGrossAmount;
	private OCRNumber totalDueAmount;
	private OCRNumber withholdingTaxRate;
	private OCRNumber withholdingTaxAmount;
	private OCRNumber totalFeesAmount;
	private OCRNumber totalDiscountAmount;
	private OCRNumber reimbursableExpensesAmount;
	private OCRNumber additionalChargesAmount;
	private OCRNumber additionalDiscountsAmount;
	private OCRString serviceAddress;
	private OCRNumber supplyNumber;
	private OCRNumber meterNumber;
	private OCRNumber totalUsage;
	private OCRNumber usageUnitOfMeasurement;
	private List<OCRInvoiceLine> lines;
	private List<OCRInvoiceBreakdown> breakdowns;
	private List<OCRInvoiceDue> dues;
	private List<OCRReading> readings;
	
	public Optional<OCRString> getCurrency() {
		return Optional.ofNullable(currency);
	}
	public OCRInvoice setCurrency(OCRString currency) {
		this.currency = currency;
		return this;
	}
	public Optional<OCRString> getLanguage() {
		return Optional.ofNullable(language);
	}
	public OCRInvoice setLanguage(OCRString language) {
		this.language = language;
		return this;
	}
	public Optional<OCRBoolean> getIsCreditNote() {
		return Optional.ofNullable(isCreditNote);
	}
	public OCRInvoice setIsCreditNote(OCRBoolean isCreditNote) {
		this.isCreditNote = isCreditNote;
		return this;
	}
	public Optional<OCRString> getNumberFormat() {
		return Optional.ofNullable(numberFormat);
	}
	public OCRInvoice setNumberFormat(OCRString numberFormat) {
		this.numberFormat = numberFormat;
		return this;
	}
	public Optional<OCRString> getInvoiceRef() {
		return Optional.ofNullable(invoiceRef);
	}
	public OCRInvoice setInvoiceRef(OCRString invoiceRef) {
		this.invoiceRef = invoiceRef;
		return this;
	}
	public Optional<OCRString> getSeriesCode() {
		return Optional.ofNullable(seriesCode);
	}
	public OCRInvoice setSeriesCode(OCRString seriesCode) {
		this.seriesCode = seriesCode;
		return this;
	}
	public Optional<OCRString> getTaxClass() {
		return Optional.ofNullable(taxClass);
	}
	public OCRInvoice setTaxClass(OCRString taxClass) {
		this.taxClass = taxClass;
		return this;
	}
	public Optional<OCRString> getIssuerCountry() {
		return Optional.ofNullable(issuerCountry);
	}
	public OCRInvoice setIssuerCountry(OCRString issuerCountry) {
		this.issuerCountry = issuerCountry;
		return this;
	}
	public Optional<OCRString> getRecipientCountry() {
		return Optional.ofNullable(recipientCountry);
	}
	public OCRInvoice setRecipientCountry(OCRString recipientCountry) {
		this.recipientCountry = recipientCountry;
		return this;
	}
	public Optional<OCRAddress> getIssuerAddressDetails() {
		return Optional.ofNullable(issuerAddressDetails);
	}
	public OCRInvoice setIssuerAddressDetails(OCRAddress issuerAddressDetails) {
		this.issuerAddressDetails = issuerAddressDetails;
		return this;
	}
	public Optional<OCRAddress> getRecipientAddressDetails() {
		return Optional.ofNullable(recipientAddressDetails);
	}
	public OCRInvoice setRecipientAddressDetails(OCRAddress recipientAddressDetails) {
		this.recipientAddressDetails = recipientAddressDetails;
		return this;
	}
	public Optional<OCRString> getDocumentNumber() {
		return Optional.ofNullable(documentNumber);
	}
	public OCRInvoice setDocumentNumber(OCRString documentNumber) {
		this.documentNumber = documentNumber;
		return this;
	}
	public Optional<OCRString> getIssueDate() {
		return Optional.ofNullable(issueDate);
	}
	public OCRInvoice setIssueDate(OCRString issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Optional<OCRString> getIssuerName() {
		return Optional.ofNullable(issuerName);
	}
	public OCRInvoice setIssuerName(OCRString issuerName) {
		this.issuerName = issuerName;
		return this;
	}
	public Optional<OCRString> getIssuerTaxId() {
		return Optional.ofNullable(issuerTaxId);
	}
	public OCRInvoice setIssuerTaxId(OCRString issuerTaxId) {
		this.issuerTaxId = issuerTaxId;
		return this;
	}
	public Optional<OCRString> getIssuerAddress() {
		return Optional.ofNullable(issuerAddress);
	}
	public OCRInvoice setIssuerAddress(OCRString issuerAddress) {
		this.issuerAddress = issuerAddress;
		return this;
	}
	public Optional<OCRString> getIssuerEmail() {
		return Optional.ofNullable(issuerEmail);
	}
	public OCRInvoice setIssuerEmail(OCRString issuerEmail) {
		this.issuerEmail = issuerEmail;
		return this;
	}
	public Optional<OCRString> getIssuerPhoneNumber() {
		return Optional.ofNullable(issuerPhoneNumber);
	}
	public OCRInvoice setIssuerPhoneNumber(OCRString issuerPhoneNumber) {
		this.issuerPhoneNumber = issuerPhoneNumber;
		return this;
	}
	public Optional<OCRString> getIssuerWebsite() {
		return Optional.ofNullable(issuerWebsite);
	}
	public OCRInvoice setIssuerWebsite(OCRString issuerWebsite) {
		this.issuerWebsite = issuerWebsite;
		return this;
	}
	public Optional<OCRString> getRecipientName() {
		return Optional.ofNullable(recipientName);
	}
	public OCRInvoice setRecipientName(OCRString recipientName) {
		this.recipientName = recipientName;
		return this;
	}
	public Optional<OCRString> getRecipientTaxId() {
		return Optional.ofNullable(recipientTaxId);
	}
	public OCRInvoice setRecipientTaxId(OCRString recipientTaxId) {
		this.recipientTaxId = recipientTaxId;
		return this;
	}
	public Optional<OCRString> getRecipientAddress() {
		return Optional.ofNullable(recipientAddress);
	}
	public OCRInvoice setRecipientAddress(OCRString recipientAddress) {
		this.recipientAddress = recipientAddress;
		return this;
	}
	public Optional<OCRString> getShippingAddress() {
		return Optional.ofNullable(shippingAddress);
	}
	public OCRInvoice setShippingAddress(OCRString shippingAddress) {
		this.shippingAddress = shippingAddress;
		return this;
	}
	public Optional<OCRString> getRecipientEmail() {
		return Optional.ofNullable(recipientEmail);
	}
	public OCRInvoice setRecipientEmail(OCRString recipientEmail) {
		this.recipientEmail = recipientEmail;
		return this;
	}
	public Optional<OCRString> getRecipientWebsite() {
		return Optional.ofNullable(recipientWebsite);
	}
	public OCRInvoice setRecipientWebsite(OCRString recipientWebsite) {
		this.recipientWebsite = recipientWebsite;
		return this;
	}
	public Optional<OCRString> getRecipientPhoneNumber() {
		return Optional.ofNullable(recipientPhoneNumber);
	}
	public OCRInvoice setRecipientPhoneNumber(OCRString recipientPhoneNumber) {
		this.recipientPhoneNumber = recipientPhoneNumber;
		return this;
	}
	public Optional<OCRString> getPaymentMethod() {
		return Optional.ofNullable(paymentMethod);
	}
	public OCRInvoice setPaymentMethod(OCRString paymentMethod) {
		this.paymentMethod = paymentMethod;
		return this;
	}
	public Optional<OCRString> getIBAN() {
		return Optional.ofNullable(IBAN);
	}
	public OCRInvoice setIBAN(OCRString iBAN) {
		IBAN = iBAN;
		return this;
	}
	public Optional<OCRString> getSWIFT() {
		return Optional.ofNullable(SWIFT);
	}
	public OCRInvoice setSWIFT(OCRString sWIFT) {
		SWIFT = sWIFT;
		return this;
	}
	public Optional<OCRString> getClientCode() {
		return Optional.ofNullable(clientCode);
	}
	public OCRInvoice setClientCode(OCRString clientCode) {
		this.clientCode = clientCode;
		return this;
	}
	public Optional<OCRString> getDeliveryNoteRef() {
		return Optional.ofNullable(deliveryNoteRef);
	}
	public OCRInvoice setDeliveryNoteRef(OCRString deliveryNoteRef) {
		this.deliveryNoteRef = deliveryNoteRef;
		return this;
	}
	public Optional<OCRString> getOrderRef() {
		return Optional.ofNullable(orderRef);
	}
	public OCRInvoice setOrderRef(OCRString orderRef) {
		this.orderRef = orderRef;
		return this;
	}
	public Optional<OCRString> getContractRef() {
		return Optional.ofNullable(contractRef);
	}
	public OCRInvoice setContractRef(OCRString contractRef) {
		this.contractRef = contractRef;
		return this;
	}
	public Optional<OCRString> getIncoterms() {
		return Optional.ofNullable(incoterms);
	}
	public OCRInvoice setIncoterms(OCRString incoterms) {
		this.incoterms = incoterms;
		return this;
	}
	public Optional<OCRString> getDocumentType() {
		return Optional.ofNullable(documentType);
	}
	public OCRInvoice setDocumentType(OCRString documentType) {
		this.documentType = documentType;
		return this;
	}
	public Optional<OCRString> getAdditionalNotes() {
		return Optional.ofNullable(additionalNotes);
	}
	public OCRInvoice setAdditionalNotes(OCRString additionalNotes) {
		this.additionalNotes = additionalNotes;
		return this;
	}
	public Optional<OCRString> getLegalNotes() {
		return Optional.ofNullable(legalNotes);
	}
	public OCRInvoice setLegalNotes(OCRString legalNotes) {
		this.legalNotes = legalNotes;
		return this;
	}
	public Optional<OCRNumber> getTotalTaxAmount() {
		return Optional.ofNullable(totalTaxAmount);
	}
	public OCRInvoice setTotalTaxAmount(OCRNumber totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
		return this;
	}
	public Optional<OCRNumber> getTotalTaxBaseAmount() {
		return Optional.ofNullable(totalTaxBaseAmount);
	}
	public OCRInvoice setTotalTaxBaseAmount(OCRNumber totalTaxBaseAmount) {
		this.totalTaxBaseAmount = totalTaxBaseAmount;
		return this;
	}
	public Optional<OCRNumber> getTotalAmount() {
		return Optional.ofNullable(totalAmount);
	}
	public OCRInvoice setTotalAmount(OCRNumber totalAmount) {
		this.totalAmount = totalAmount;
		return this;
	}
	public Optional<OCRNumber> getTotalGrossAmount() {
		return Optional.ofNullable(totalGrossAmount);
	}
	public OCRInvoice setTotalGrossAmount(OCRNumber totalGrossAmount) {
		this.totalGrossAmount = totalGrossAmount;
		return this;
	}
	public Optional<OCRNumber> getTotalDueAmount() {
		return Optional.ofNullable(totalDueAmount);
	}
	public OCRInvoice setTotalDueAmount(OCRNumber totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
		return this;
	}
	public Optional<OCRNumber> getWithholdingTaxRate() {
		return Optional.ofNullable(withholdingTaxRate);
	}
	public OCRInvoice setWithholdingTaxRate(OCRNumber withholdingTaxRate) {
		this.withholdingTaxRate = withholdingTaxRate;
		return this;
	}
	public Optional<OCRNumber> getWithholdingTaxAmount() {
		return Optional.ofNullable(withholdingTaxAmount);
	}
	public OCRInvoice setWithholdingTaxAmount(OCRNumber withholdingTaxAmount) {
		this.withholdingTaxAmount = withholdingTaxAmount;
		return this;
	}
	public Optional<OCRNumber> getTotalFeesAmount() {
		return Optional.ofNullable(totalFeesAmount);
	}
	public OCRInvoice setTotalFeesAmount(OCRNumber totalFeesAmount) {
		this.totalFeesAmount = totalFeesAmount;
		return this;
	}
	public Optional<OCRNumber> getTotalDiscountAmount() {
		return Optional.ofNullable(totalDiscountAmount);
	}
	public OCRInvoice setTotalDiscountAmount(OCRNumber totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
		return this;
	}
	public Optional<OCRNumber> getReimbursableExpensesAmount() {
		return Optional.ofNullable(reimbursableExpensesAmount);
	}
	public OCRInvoice setReimbursableExpensesAmount(OCRNumber reimbursableExpensesAmount) {
		this.reimbursableExpensesAmount = reimbursableExpensesAmount;
		return this;
	}
	public Optional<OCRNumber> getAdditionalChargesAmount() {
		return Optional.ofNullable(additionalChargesAmount);
	}
	public OCRInvoice setAdditionalChargesAmount(OCRNumber additionalChargesAmount) {
		this.additionalChargesAmount = additionalChargesAmount;
		return this;
	}
	public Optional<OCRNumber> getAdditionalDiscountsAmount() {
		return Optional.ofNullable(additionalDiscountsAmount);
	}
	public OCRInvoice setAdditionalDiscountsAmount(OCRNumber additionalDiscountsAmount) {
		this.additionalDiscountsAmount = additionalDiscountsAmount;
		return this;
	}
	
	public Optional<OCRString> getServiceAddress() {
		return Optional.ofNullable(serviceAddress);
	}
	public OCRInvoice setServiceAddress(OCRString serviceAddress) {
		this.serviceAddress = serviceAddress;
		return this;
	}
	public Optional<OCRNumber> getSupplyNumber() {
		return Optional.ofNullable(supplyNumber);
	}
	public OCRInvoice setSupplyNumber(OCRNumber supplyNumber) {
		this.supplyNumber = supplyNumber;
		return this;
	}
	public Optional<OCRNumber> getMeterNumber() {
		return Optional.ofNullable(meterNumber);
	}
	public OCRInvoice setMeterNumber(OCRNumber meterNumber) {
		this.meterNumber = meterNumber;
		return this;
	}
	
	public Optional<OCRNumber> getTotalUsage() {
		return Optional.ofNullable(totalUsage);
	}
	public OCRInvoice setTotalUsage(OCRNumber totalUsage) {
		this.totalUsage = totalUsage;
		return this;
	}
	
	public Optional<OCRNumber> getUsageUnitOfMeasurement() {
		return Optional.ofNullable(usageUnitOfMeasurement);
	}
	public OCRInvoice setUsageUnitOfMeasurement(OCRNumber usageUnitOfMeasurement) {
		this.usageUnitOfMeasurement = usageUnitOfMeasurement;
		return this;
	}
	
	public Optional<List<OCRInvoiceLine>> getLines() {
		return Optional.ofNullable(lines);
	}
	public OCRInvoice setLines(List<OCRInvoiceLine> lines) {
		this.lines = lines;
		return this;
	}
	
	public Optional<List<OCRInvoiceBreakdown>> getBreakdowns() {
		return Optional.ofNullable(breakdowns);
	}
	public OCRInvoice setBreakdowns(List<OCRInvoiceBreakdown> breakdowns) {
		this.breakdowns = breakdowns;
		return this;		
	}
	
	public Optional<List<OCRInvoiceDue>> getDues() {
		return Optional.ofNullable(dues);
	}
	public OCRInvoice setDues(List<OCRInvoiceDue> dues) {
		this.dues = dues;
		return this;
	}
	
	public Optional<List<OCRReading>> getReadings() {
		return Optional.ofNullable(readings);
	}
	public OCRInvoice setReadings(List<OCRReading> readings) {
		this.readings = readings;
		return this;
	}
	
	
}
