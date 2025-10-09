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
	private OCRString documentNumber;
	private OCRString issueDate;
	
	private OCRRegistry supplier;
	private OCRRegistry customer;

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

	public Optional<OCRRegistry> getSupplier() {
		return Optional.ofNullable(supplier);
	}
	
	public OCRInvoice setSupplier(OCRRegistry supplier) {
		this.supplier = supplier;
		return this;
	}
	
	public Optional<OCRRegistry> getCustomer() {
		return Optional.ofNullable(customer);
	}
	
	public OCRInvoice setCustomer(OCRRegistry customer) {
		this.customer = customer;
		return this;
	}
		
	public Optional<String> getReferenceCode() {
		StringBuilder reference = null;
		Optional<String> optSeries = getSeriesCode().flatMap( o -> o.getValue() );
		if (optSeries.isPresent()) {
			reference = new StringBuilder();
			reference.append( optSeries.get() );
		}
		Optional<String> optDocument = getDocumentNumber().flatMap( o -> o.getValue() );
		if (optDocument.isPresent()) {
			if (reference == null) {
				reference = new StringBuilder();
			} else {
				reference.append("/");
			}
			reference.append(optDocument.get());
		}
		return (reference == null)?Optional.ofNullable(null): Optional.ofNullable(reference.toString()); 
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
	
	public String getSupplierDocument() {
		return getSupplier().flatMap(c -> c.getTaxId()).flatMap(t -> t.getValue()).orElse(null);
	}

	public String getSupplierCountry() {
		return getSupplier().flatMap(c -> c.getCountry()).flatMap(t -> t.getValue()).orElse(null);
	}
	
	public String getCustomerDocument() {
		return getCustomer().flatMap(c -> c.getTaxId()).flatMap(t -> t.getValue()).orElse(null);
	}
	
	public String getCustomerCountry() {
		return getCustomer().flatMap(c -> c.getCountry()).flatMap(t -> t.getValue()).orElse(null);
	}
	
}
