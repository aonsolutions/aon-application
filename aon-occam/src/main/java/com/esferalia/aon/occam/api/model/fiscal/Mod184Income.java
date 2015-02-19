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
	private int epigraph;
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

	private boolean dirty;
	private boolean deleted;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public int getMod184() {
		return mod184;
	}

	public void setMod184(int mod184) {
		this.mod184 = mod184;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSubKey() {
		return subKey;
	}

	public void setSubKey(String subKey) {
		this.subKey = subKey;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public byte getRegime() {
		return regime;
	}

	public void setRegime(byte regime) {
		this.regime = regime;
	}

	public byte getActivityType() {
		return activityType;
	}

	public void setActivityType(byte activityType) {
		this.activityType = activityType;
	}

	public Integer getEpigraph() {
		return epigraph;
	}

	public void setEpigraph(Integer epigraph) {
		this.epigraph = epigraph;
	}

	public String getGranteeDocument() {
		return granteeDocument;
	}

	public void setGranteeDocument(String granteeDocument) {
		this.granteeDocument = granteeDocument;
	}

	public String getGranteeName() {
		return granteeName;
	}

	public void setGranteeName(String granteeName) {
		this.granteeName = granteeName;
	}

	public Date getAdqDate() {
		return adqDate;
	}

	public void setAdqDate(Date adqDate) {
		this.adqDate = adqDate;
	}

	public double getIncrease() {
		return increase;
	}

	public void setIncrease(double increase) {
		this.increase = increase;
	}

	public double getDecrease() {
		return decrease;
	}

	public void setDecrease(double decrease) {
		this.decrease = decrease;
	}

	public double getAccountingResult() {
		return accountingResult;
	}

	public void setAccountingResult(double accountingResult) {
		this.accountingResult = accountingResult;
	}

	public double getExpenses() {
		return expenses;
	}

	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}

	public double getNetYield() {
		return netYield;
	}

	public void setNetYield(double netYield) {
		this.netYield = netYield;
	}

	public double getReductionPercent() {
		return reductionPercent;
	}

	public void setReductionPercent(double reductionPercent) {
		this.reductionPercent = reductionPercent;
	}

	public double getDeductionRightRent() {
		return deductionRightRent;
	}

	public void setDeductionRightRent(double deductionRightRent) {
		this.deductionRightRent = deductionRightRent;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public double getDeductionBase() {
		return deductionBase;
	}

	public void setDeductionBase(double deductionBase) {
		this.deductionBase = deductionBase;
	}

	public double getRetention() {
		return retention;
	}

	public void setRetention(double retention) {
		this.retention = retention;
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
