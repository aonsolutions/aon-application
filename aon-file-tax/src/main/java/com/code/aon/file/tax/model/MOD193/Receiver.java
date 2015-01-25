package com.code.aon.file.tax.model.MOD193;

import com.code.aon.file.tax.FileTaxUtil;

public class Receiver {

	private String document;
	private String representativeDocument;
	private String name;
	private String intermediaryPayment;
	private int province;
	private String keyCode;
	private String issuingCode;
	private String key;
	private String nature;
	private String payment;
	private String codeType;
	private String accountCode;
	private String pending;
	private int accrualYear;
	private String inKind;
	private double lenderAmount;
	private double reduction;
	private double retentionBase;
	private double percent;
	private double retention;
	private int loanStartDate;
	private int loanDueDate;
	private double compensation;
	private double guarantee;

	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = FileTaxUtil.changeInvalidCharacters(document);
	}
	public String getRepresentativeDocument() {
		return representativeDocument;
	}
	public void setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = FileTaxUtil.changeInvalidCharacters(representativeDocument);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = FileTaxUtil.changeInvalidCharacters(name);
	}
	public int getProvince() {
		return province;
	}
	public void setProvince(int province) {
		this.province = province;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getNature() {
		return nature;
	}
	public void setNature(String nature) {
		this.nature = nature;
	}
	public String getIntermediaryPayment() {
		return intermediaryPayment;
	}
	public void setIntermediaryPayment(String intermediaryPayment) {
		this.intermediaryPayment = intermediaryPayment;
	}
	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	public String getIssuingCode() {
		return issuingCode;
	}
	public void setIssuingCode(String issuingCode) {
		this.issuingCode = issuingCode;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getCodeType() {
		return codeType;
	}
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getPending() {
		return pending;
	}
	public void setPending(String pending) {
		this.pending = pending;
	}
	public int getAccrualYear() {
		return accrualYear;
	}
	public void setAccrualYear(int accrualYear) {
		this.accrualYear = accrualYear;
	}
	public String getInKind() {
		return inKind;
	}
	public void setInKind(String inKind) {
		this.inKind = inKind;
	}
	public double getLenderAmount() {
		return lenderAmount;
	}
	public void setLenderAmount(double lenderAmount) {
		this.lenderAmount = lenderAmount;
	}
	public double getReduction() {
		return reduction;
	}
	public void setReduction(double reduction) {
		this.reduction = reduction;
	}
	public double getRetentionBase() {
		return retentionBase;
	}
	public void setRetentionBase(double retentionBase) {
		this.retentionBase = retentionBase;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	public double getRetention() {
		return retention;
	}
	public void setRetention(double retention) {
		this.retention = retention;
	}
	public int getLoanStartDate() {
		return loanStartDate;
	}
	public void setLoanStartDate(int loanStartDate) {
		this.loanStartDate = loanStartDate;
	}
	public int getLoanDueDate() {
		return loanDueDate;
	}
	public void setLoanDueDate(int loanDueDate) {
		this.loanDueDate = loanDueDate;
	}
	public double getCompensation() {
		return compensation;
	}
	public void setCompensation(double compensation) {
		this.compensation = compensation;
	}
	public double getGuarantee() {
		return guarantee;
	}
	public void setGuarantee(double guarantee) {
		this.guarantee = guarantee;
	}
	
}
