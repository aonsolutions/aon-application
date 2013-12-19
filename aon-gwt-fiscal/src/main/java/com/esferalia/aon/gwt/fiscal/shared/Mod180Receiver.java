package com.esferalia.aon.gwt.fiscal.shared;

@SuppressWarnings("serial")
public class Mod180Receiver extends Mod180Detail {

	private int domain;
	private int mod180;

	private String document;
	private String representativeDocument;
	private int province;
	private boolean inKind;
	private double percent;
	private double perception;
	private double retention;
	private int accrualYear;

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public int getMod180() {
		return mod180;
	}

	public void setMod180(int mod180) {
		this.mod180 = mod180;
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

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}

	public double getPerception() {
		return perception;
	}

	public void setPerception(double perception) {
		this.perception = perception;
	}

	public double getRetention() {
		return retention;
	}

	public void setRetention(double retention) {
		this.retention = retention;
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

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}
}
