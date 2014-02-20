package com.code.aon.file.tax.model.MOD340.data;



public class Invoice implements Cloneable {

	private String type;
	private Integer year;
	private String period;
	private String code;
	private String document;
	private String name;
	private String country;
	private String countryKey;
	private String countryCode;
	private String countryNif;
	private String operation;
	private String issueDate;
	private String operationDate;
	private double percent;
	private double taxableBase;
	private double quota;
	private double total;
	private double costTaxableBase;
	private String invoiceNumber;
	private String documentNumber;
	private int invoiceCount;
	private int registerCount;
	private String firstInvoiceNumber;
	private String lastInvoiceNumber;
	private String rectifiedInvoiceNumber;
	private double surchargePercent;
	private double surchargeQuota;
	private double deductibleQuota;
	private String intracommunitaryType;
	private String declaredKey;
	private int operationPeriod;
	private String description;
	private String address;
	private String city;
	private String zip;
	private String other;
	private int yearProrate;
	private int yearRegularization;
	private String deliveryInvoice;
	private double doneRegularization;
	private String investementDate;
	private String investementName;
	
	private String financeDate = "00000000";
	private double financeAmount;
	private String financeType;
	private String financeBank;
	
	

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCountryKey() {
		return countryKey;
	}
	public void setCountryKey(String countryKey) {
		this.countryKey = countryKey;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryNif() {
		return countryNif;
	}
	public void setCountryNif(String countryNif) {
		this.countryNif = countryNif;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public String getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(String operationDate) {
		this.operationDate = operationDate;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	public double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}
	public double getQuota() {
		return quota;
	}
	public void setQuota(double quota) {
		this.quota = quota;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getCostTaxableBase() {
		return costTaxableBase;
	}
	public void setCostTaxableBase(double costTaxableBase) {
		this.costTaxableBase = costTaxableBase;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public int getInvoiceCount() {
		return invoiceCount;
	}
	public void setInvoiceCount(int invoiceCount) {
		this.invoiceCount = invoiceCount;
	}
	public int getRegisterCount() {
		return registerCount;
	}
	public void setRegisterCount(int registerCount) {
		this.registerCount = registerCount;
	}
	public String getFirstInvoiceNumber() {
		return firstInvoiceNumber;
	}
	public void setFirstInvoiceNumber(String firstInvoiceNumber) {
		this.firstInvoiceNumber = firstInvoiceNumber;
	}
	public String getLastInvoiceNumber() {
		return lastInvoiceNumber;
	}
	public void setLastInvoiceNumber(String lastInvoiceNumber) {
		this.lastInvoiceNumber = lastInvoiceNumber;
	}
	public String getRectifiedInvoiceNumber() {
		return rectifiedInvoiceNumber;
	}
	public void setRectifiedInvoiceNumber(String rectifiedInvoiceNumber) {
		this.rectifiedInvoiceNumber = rectifiedInvoiceNumber;
	}
	public double getSurchargePercent() {
		return surchargePercent;
	}
	public void setSurchargePercent(double surchargePercent) {
		this.surchargePercent = surchargePercent;
	}
	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public void setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
	}
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public void setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
	}
	public String getIntracommunitaryType() {
		return intracommunitaryType;
	}
	public void setIntracommunitaryType(String intracommunitaryType) {
		this.intracommunitaryType = intracommunitaryType;
	}
	public String getDeclaredKey() {
		return declaredKey;
	}
	public void setDeclaredKey(String declaredKey) {
		this.declaredKey = declaredKey;
	}
	public int getOperationPeriod() {
		return operationPeriod;
	}
	public void setOperationPeriod(int operationPeriod) {
		this.operationPeriod = operationPeriod;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public int getYearProrate() {
		return yearProrate;
	}
	public void setYearProrate(int yearProrate) {
		this.yearProrate = yearProrate;
	}
	public int getYearRegularization() {
		return yearRegularization;
	}
	public void setYearRegularization(int yearRegularization) {
		this.yearRegularization = yearRegularization;
	}
	public String getDeliveryInvoice() {
		return deliveryInvoice;
	}
	public void setDeliveryInvoice(String deliveryInvoice) {
		this.deliveryInvoice = deliveryInvoice;
	}
	public double getDoneRegularization() {
		return doneRegularization;
	}
	public void setDoneRegularization(double doneRegularization) {
		this.doneRegularization = doneRegularization;
	}
	public String getInvestementDate() {
		return investementDate;
	}
	public void setInvestementDate(String investementDate) {
		this.investementDate = investementDate;
	}
	public String getInvestementName() {
		return investementName;
	}
	public void setInvestementName(String investementName) {
		this.investementName = investementName;
	}
	public String getFinanceDate() {
		return financeDate;
	}
	public void setFinanceDate(String financeDate) {
		this.financeDate = financeDate;
	}
	public double getFinanceAmount() {
		return financeAmount;
	}
	public void setFinanceAmount(double financeAmount) {
		this.financeAmount = financeAmount;
	}
	public String getFinanceType() {
		return financeType;
	}
	public void setFinanceType(String financeType) {
		this.financeType = financeType;
	}
	public String getFinanceBank() {
		return financeBank;
	}
	public void setFinanceBank(String financeBank) {
		this.financeBank = financeBank;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	public Invoice cloneInvoice() {
		try {
			return (Invoice) clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
