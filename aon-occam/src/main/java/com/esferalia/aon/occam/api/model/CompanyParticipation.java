package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CompanyParticipation implements Serializable {

	private static final long serialVersionUID = 3400183583793598978L;
	
	private String document;
	private String name;
	private int province;
	private String country;
	private boolean representative;
	private double percent;
	private double nominalValue;
	private double bookValue;
	private double incomes;
	private double aValue;
	private double bValue;
	private double cValue;
	private double ccValue;
	private double dValue;
	private double capital;
	private double reserve;
	private double otherAmounts;
	private double result;

	public String getDocument() {
		return document;
	}

	public CompanyParticipation setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public CompanyParticipation setName(String name) {
		this.name = name;
		return this;
	}

	public int getProvince() {
		return province;
	}

	public CompanyParticipation setProvince(int province) {
		this.province = province;
		return this;
	}
	public String getCountry() {
		return country;
	}

	public CompanyParticipation setCountry(String country) {
		this.country = country;
		return this;
	}

	public boolean isRepresentative() {
		return representative;
	}
	public CompanyParticipation setRepresentative(boolean representative) {
		this.representative = representative;
		return this;
	}

	public double getPercent() {
		return percent;
	}

	public CompanyParticipation setPercent(double percent) {
		this.percent = percent;
		return this;
	}

	public double getNominalValue() {
		return nominalValue;
	}

	public CompanyParticipation setNominalValue(double nominalValue) {
		this.nominalValue = nominalValue;
		return this;
	}

	public double getBookValue() {
		return bookValue;
	}

	public CompanyParticipation setBookValue(double bookValue) {
		this.bookValue = bookValue;
		return this;
	}

	public double getIncomes() {
		return incomes;
	}

	public CompanyParticipation setIncomes(double incomes) {
		this.incomes = incomes;
		return this;
	}

	public double getaValue() {
		return aValue;
	}

	public CompanyParticipation setaValue(double aValue) {
		this.aValue = aValue;
		return this;
	}

	public double getbValue() {
		return bValue;
	}

	public CompanyParticipation setbValue(double bValue) {
		this.bValue = bValue;
		return this;
	}

	public double getcValue() {
		return cValue;
	}

	public CompanyParticipation setcValue(double cValue) {
		this.cValue = cValue;
		return this;
	}

	public double getccValue() {
		return ccValue;
	}

	public CompanyParticipation setccValue(double ccValue) {
		this.ccValue = ccValue;
		return this;
	}

	public double getdValue() {
		return dValue;
	}

	public CompanyParticipation setdValue(double dValue) {
		this.dValue = dValue;
		return this;
	}

	public double getCapital() {
		return capital;
	}

	public CompanyParticipation setCapital(double capital) {
		this.capital = capital;
		return this;
	}

	public double getReserve() {
		return reserve;
	}

	public CompanyParticipation setReserve(double reserve) {
		this.reserve = reserve;
		return this;
	}

	public double getOtherAmounts() {
		return otherAmounts;
	}

	public CompanyParticipation setOtherAmounts(double otherAmounts) {
		this.otherAmounts = otherAmounts;
		return this;
	}

	public double getResult() {
		return result;
	}

	public CompanyParticipation setResult(double result) {
		this.result = result;
		return this;
	}

	public String getEntity() {
		if (AonStringUtils.isEmpty(document)) {
			return null;
		}
		return AonDocumentUtil.isEntity(document)?"J":"F";
	}
	public String getProvinceStr() {
		String p = Integer.toString(province);
		return AonStringUtils.isEmpty(getDocument())
				?null
				:AonStringUtils.leftPad(p, 2, "0");
	}
	public String getRepresenStr() {
		return isRepresentative()?"1":"0"; 
	}
	
}
