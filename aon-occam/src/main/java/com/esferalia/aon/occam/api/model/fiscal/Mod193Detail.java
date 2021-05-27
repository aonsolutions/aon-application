package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod193Detail implements Serializable {
	
	private static final long serialVersionUID = -8179709468567724824L;
	
	public static final String DETAIL_TYPE = "P";
	public static final String EXPENSE_TYPE = "G";
	
	private int tempId;

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
	
	private double penalization;
	private boolean declarantNature;
	
	private boolean dirty;
	private boolean deleted;

	public int getTempId() {
		return tempId;
	}

	public Mod193Detail setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}
	
	public Integer getId() {
		return id;
	}

	public Mod193Detail setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getType() {
		return type;
	}
	public Mod193Detail setType(String type) {
		this.type = type;
		return this;
	}
	public boolean isExpense() {
		return AonStringUtils.equals( EXPENSE_TYPE, getType() );
	}
	public String getName() {
		return name;
	}

	public Mod193Detail setName(String name) {
		this.name = name;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod193Detail setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod193() {
		return mod193;
	}

	public Mod193Detail setMod193(int mod193) {
		this.mod193 = mod193;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod193Detail setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public Mod193Detail setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
		return this;
	}
	
	public boolean isIntermediaryPayment() {
		return intermediaryPayment;
	}

	public Mod193Detail setIntermediaryPayment(boolean intermediaryPayment) {
		this.intermediaryPayment = intermediaryPayment;
		return this;
	}

	public int getProvince() {
		return province;
	}

	public Mod193Detail setProvince(int province) {
		this.province = province;
		return this;
	}

	public byte getKeyCode() {
		return keyCode;
	}

	public Mod193Detail setKeyCode(byte keyCode) {
		this.keyCode = keyCode;
		return this;
	}

	public String getIssuingCode() {
		return issuingCode;
	}

	public Mod193Detail setIssuingCode(String issuingCode) {
		this.issuingCode = issuingCode;
		return this;
	}

	public String getKey() {
		return key;
	}

	public Mod193Detail setKey(String key) {
		this.key = key;
		return this;
	}

	public String getNature() {
		return nature;
	}

	public Mod193Detail setNature(String nature) {
		this.nature = nature;
		return this;
	}

	public byte getPayment() {
		return payment;
	}

	public Mod193Detail setPayment(byte payment) {
		this.payment = payment;
		return this;
	}

	public String getCodeType() {
		return codeType;
	}

	public Mod193Detail setCodeType(String codeType) {
		this.codeType = codeType;
		return this;
	}

	public double getLenderAmount() {
		return lenderAmount;
	}

	public Mod193Detail setLenderAmount(double lenderAmount) {
		this.lenderAmount = lenderAmount;
		return this;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public Mod193Detail setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public boolean isPending() {
		return pending;
	}

	public Mod193Detail setPending(boolean pending) {
		this.pending = pending;
		return this;
	}

	public int getAccrualYear() {
		return accrualYear;
	}

	public Mod193Detail setAccrualYear(int accrualYear) {
		this.accrualYear = accrualYear;
		return this;
	}

	public boolean isInKind() {
		return inKind;
	}

	public Mod193Detail setInKind(boolean inKind) {
		this.inKind = inKind;
		return this;
	}

	public double getPerception() {
		return perception;
	}

	public Mod193Detail setPerception(double perception) {
		this.perception = perception;
		return this;
	}

	public double getReduction() {
		return reduction;
	}

	public Mod193Detail setReduction(double reduction) {
		this.reduction = reduction;
		return this;
	}

	public double getRetentionBase() {
		return retentionBase;
	}

	public Mod193Detail setRetentionBase(double retentionBase) {
		this.retentionBase = retentionBase;
		return this;
	}

	public double getPercent() {
		return percent;
	}

	public Mod193Detail setPercent(double percent) {
		this.percent = percent;
		return this;
	}

	public double getRetention() {
		return retention;
	}

	public Mod193Detail setRetention(double retention) {
		this.retention = retention;
		return this;
	}

	public boolean isDeponentNature() {
		return deponentNature;
	}

	public Mod193Detail setDeponentNature(boolean deponentNature) {
		this.deponentNature = deponentNature;
		return this;
	}

	public Date getLoanStartDate() {
		return loanStartDate;
	}

	public Mod193Detail setLoanStartDate(Date loanStartDate) {
		this.loanStartDate = loanStartDate;
		return this;
	}

	public Date getLoanDueDate() {
		return loanDueDate;
	}

	public Mod193Detail setLoanDueDate(Date loanDueDate) {
		this.loanDueDate = loanDueDate;
		return this;
	}

	public double getCompensation() {
		return compensation;
	}

	public Mod193Detail setCompensation(double compensation) {
		this.compensation = compensation;
		return this;
	}

	public double getGuarantee() {
		return guarantee;
	}

	public Mod193Detail setGuarantee(double guarantee) {
		this.guarantee = guarantee;
		return this;
	}
	
	public double getExpenses() {
		return expenses;
	}

	public Mod193Detail setExpenses(double expenses) {
		this.expenses = expenses;
		return this;
	}

	public double getPenalization() {
		return penalization;
	}

	public Mod193Detail setPenalization(double penalization) {
		this.penalization = penalization;
		return this;
	}

	public boolean isDeclarantNature() {
		return declarantNature;
	}

	public Mod193Detail setDeclarantNature(boolean declarantNature) {
		this.declarantNature = declarantNature;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Mod193Detail setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod193Detail setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

}
