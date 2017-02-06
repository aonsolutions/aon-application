package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Mod184Income implements Serializable {

	private Integer id;
	private int domain;
	private int mod184;
	
	private String key;
	private String subKey;
	private String country;
	private byte regime;
	private byte activityType;
	private Integer epigraph;
	private String granteeDocument;
	private String granteeName;
	private Date adqDate;
	private double increase;
	private double decrease;
	private double accountingResult;
	private double expenses;
	private double netYield;
	private double reductionPercent;
	private double deductionRightRent;
	private double result;
	private double deductionBase;
	private double retention;
	private String location;
	private String cadasdralReference;
	private double staffExpenses;
	private double assetAcquisition;
	private double taxDeduction;
	private double otherTaxDeduction;
	private boolean vatAccrualPayment; 

	private boolean dirty;
	private boolean deleted;
	private int tempId;	

	public Integer getId() {
		return id;
	}

	public Mod184Income setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod184Income setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod184() {
		return mod184;
	}

	public Mod184Income setMod184(int mod184) {
		this.mod184 = mod184;
		return this;
	}
	
	public String getKey() {
		return key;
	}

	public Mod184Income setKey(String key) {
		this.key = key;
		return this;
	}

	public String getSubKey() {
		return subKey;
	}

	public Mod184Income setSubKey(String subKey) {
		this.subKey = subKey;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public Mod184Income setCountry(String country) {
		this.country = country;
		return this;
	}

	public byte getRegime() {
		return regime;
	}

	public Mod184Income setRegime(byte regime) {
		this.regime = regime;
		return this;
	}

	public byte getActivityType() {
		return activityType;
	}

	public Mod184Income setActivityType(byte activityType) {
		this.activityType = activityType;
		return this;
	}

	public Integer getEpigraph() {
		return epigraph;
	}

	public Mod184Income setEpigraph(Integer epigraph) {
		this.epigraph = epigraph;
		return this;
	}

	public String getGranteeDocument() {
		return granteeDocument;
	}

	public Mod184Income setGranteeDocument(String granteeDocument) {
		this.granteeDocument = granteeDocument;
		return this;
	}

	public String getGranteeName() {
		return granteeName;
	}

	public Mod184Income setGranteeName(String granteeName) {
		this.granteeName = granteeName;
		return this;
	}

	public Date getAdqDate() {
		return adqDate;
	}

	public Mod184Income setAdqDate(Date adqDate) {
		this.adqDate = adqDate;
		return this;
	}

	public double getIncrease() {
		return increase;
	}

	public Mod184Income setIncrease(double increase) {
		this.increase = increase;
		return this;
	}

	public double getDecrease() {
		return decrease;
	}

	public Mod184Income setDecrease(double decrease) {
		this.decrease = decrease;
		return this;
	}

	public double getAccountingResult() {
		return accountingResult;
	}

	public Mod184Income setAccountingResult(double accountingResult) {
		this.accountingResult = accountingResult;
		return this;
	}

	public double getExpenses() {
		return expenses;
	}

	public Mod184Income setExpenses(double expenses) {
		this.expenses = expenses;
		return this;
	}

	public double getNetYield() {
		return netYield;
	}

	public Mod184Income setNetYield(double netYield) {
		this.netYield = netYield;
		return this;
	}

	public double getReductionPercent() {
		return reductionPercent;
	}

	public Mod184Income setReductionPercent(double reductionPercent) {
		this.reductionPercent = reductionPercent;
		return this;
	}

	public double getDeductionRightRent() {
		return deductionRightRent;
	}

	public Mod184Income setDeductionRightRent(double deductionRightRent) {
		this.deductionRightRent = deductionRightRent;
		return this;
	}

	public double getResult() {
		return result;
	}

	public Mod184Income setResult(double result) {
		this.result = result;
		return this;
	}

	public double getDeductionBase() {
		return deductionBase;
	}

	public Mod184Income setDeductionBase(double deductionBase) {
		this.deductionBase = deductionBase;
		return this;
	}

	public double getRetention() {
		return retention;
	}

	public Mod184Income setRetention(double retention) {
		this.retention = retention;
		return this;
	}
	public String getLocation() {
		return location;
	}

	public Mod184Income setLocation(String location) {
		this.location = location;
		return this;
	}

	public String getCadasdralReference() {
		return cadasdralReference;
	}

	public Mod184Income setCadasdralReference(String cadasdralReference) {
		this.cadasdralReference = cadasdralReference;
		return this;
	}
	
	public double getStaffExpenses() {
		return staffExpenses;
	}

	public Mod184Income setStaffExpenses(double staffExpenses) {
		this.staffExpenses = staffExpenses;
		return this;
	}

	public double getAssetAcquisition() {
		return assetAcquisition;
	}

	public Mod184Income setAssetAcquisition(double assetAcquisition) {
		this.assetAcquisition = assetAcquisition;
		return this;
	}

	public double getTaxDeduction() {
		return taxDeduction;
	}

	public Mod184Income setTaxDeduction(double taxDeduction) {
		this.taxDeduction = taxDeduction;
		return this;
	}

	public double getOtherTaxDeduction() {
		return otherTaxDeduction;
	}

	public Mod184Income setOtherTaxDeduction(double otherTaxDeduction) {
		this.otherTaxDeduction = otherTaxDeduction;
		return this;
	}
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Mod184Income setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	public boolean isDirty() {
		return dirty;
	}

	public Mod184Income setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod184Income setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public int getTempId() {
		return tempId;
	}

	public Mod184Income setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}

	
}
