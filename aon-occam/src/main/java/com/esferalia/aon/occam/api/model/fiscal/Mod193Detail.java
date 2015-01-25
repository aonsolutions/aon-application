package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public class Mod193Detail implements Serializable {
	
	public static final String DETAIL_TYPE = "P";
	public static final String EXPENSE_TYPE = "G";
	
	private Integer id;
	private String type;
	private String name;
	private int domain;
	private int mod193;
	private boolean pending;
	private String document;
	private String representativeDocument;
	private boolean intermediaryPayment;
	private int province;
	private byte keyCode;
	private String issuingCode;
	private String key;
	private String nature;
	private byte payment;
	private String codeType;
	private double lenderAmount;
	private String accountCode;
	private int accrualYear;
	private boolean inKind;
	private double perception;
	private double reduction;
	private double retentionBase;
	private double percent;
	private double retention;
	private boolean deponentNature;
	private Date loanStartDate;
	private Date loanDueDate;
	private double compensation;
	private double guarantee;
	private double expenses;
	
	private boolean dirty;
	private boolean deleted;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isExpense() {
		return AonStringUtils.equals( EXPENSE_TYPE, getType() );
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public int getMod193() {
		return mod193;
	}

	public void setMod193(int mod193) {
		this.mod193 = mod193;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public void setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
	}
	
	public boolean isIntermediaryPayment() {
		return intermediaryPayment;
	}

	public void setIntermediaryPayment(boolean intermediaryPayment) {
		this.intermediaryPayment = intermediaryPayment;
	}

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}

	public byte getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(byte keyCode) {
		this.keyCode = keyCode;
	}

	public String getIssuingCode() {
		return issuingCode;
	}

	public void setIssuingCode(String issuingCode) {
		this.issuingCode = issuingCode;
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

	public byte getPayment() {
		return payment;
	}

	public void setPayment(byte payment) {
		this.payment = payment;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public double getLenderAmount() {
		return lenderAmount;
	}

	public void setLenderAmount(double lenderAmount) {
		this.lenderAmount = lenderAmount;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public int getAccrualYear() {
		return accrualYear;
	}

	public void setAccrualYear(int accrualYear) {
		this.accrualYear = accrualYear;
	}

	public boolean isInKind() {
		return inKind;
	}

	public void setInKind(boolean inKind) {
		this.inKind = inKind;
	}

	public double getPerception() {
		return perception;
	}

	public void setPerception(double perception) {
		this.perception = perception;
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

	public boolean isDeponentNature() {
		return deponentNature;
	}

	public void setDeponentNature(boolean deponentNature) {
		this.deponentNature = deponentNature;
	}

	public Date getLoanStartDate() {
		return loanStartDate;
	}

	public void setLoanStartDate(Date loanStartDate) {
		this.loanStartDate = loanStartDate;
	}

	public Date getLoanDueDate() {
		return loanDueDate;
	}

	public void setLoanDueDate(Date loanDueDate) {
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
	
	public double getExpenses() {
		return expenses;
	}

	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
