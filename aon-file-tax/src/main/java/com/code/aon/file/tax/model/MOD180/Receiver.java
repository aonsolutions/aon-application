package com.code.aon.file.tax.model.MOD180;

import com.code.aon.file.tax.FileTaxUtil;

public class Receiver {

	private String document;
	private String representativeDocument;
	private String name;
	private int province;
	private String inKind;
	private double perception;
	private double retention;
	private double percent;
	private int accrualYear;

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
		this.representativeDocument = FileTaxUtil
				.changeInvalidCharacters(representativeDocument);
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

	public String getInKind() {
		return inKind;
	}

	public void setInKind(String inKind) {
		this.inKind = inKind;
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

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public int getAccrualYear() {
		return accrualYear;
	}

	public void setAccrualYear(int accrualYear) {
		this.accrualYear = accrualYear;
	}
}
