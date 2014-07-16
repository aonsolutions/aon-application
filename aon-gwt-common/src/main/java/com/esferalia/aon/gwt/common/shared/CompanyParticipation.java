package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CompanyParticipation implements Serializable, IsSerializable {

	private static final long serialVersionUID = 1439647684878998653L;

	private String document;
	private String name;
	private int province;
	private boolean representative;
	private double percent;
	private double nominalValue;
	private double bookValue;
	private double incomes;
	private double aValue;
	private double bValue;
	private double cValue;
	private double dValue;
	private double capital;
	private double reserve;
	private double otherAmounts;
	private double result;

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

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}
	public boolean isRepresentative() {
		return representative;
	}
	public void setRepresentative(boolean representative) {
		this.representative = representative;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double getNominalValue() {
		return nominalValue;
	}

	public void setNominalValue(double nominalValue) {
		this.nominalValue = nominalValue;
	}

	public double getBookValue() {
		return bookValue;
	}

	public void setBookValue(double bookValue) {
		this.bookValue = bookValue;
	}

	public double getIncomes() {
		return incomes;
	}

	public void setIncomes(double incomes) {
		this.incomes = incomes;
	}

	public double getaValue() {
		return aValue;
	}

	public void setaValue(double aValue) {
		this.aValue = aValue;
	}

	public double getbValue() {
		return bValue;
	}

	public void setbValue(double bValue) {
		this.bValue = bValue;
	}

	public double getcValue() {
		return cValue;
	}

	public void setcValue(double cValue) {
		this.cValue = cValue;
	}

	public double getdValue() {
		return dValue;
	}

	public void setdValue(double dValue) {
		this.dValue = dValue;
	}

	public double getCapital() {
		return capital;
	}

	public void setCapital(double capital) {
		this.capital = capital;
	}

	public double getReserve() {
		return reserve;
	}

	public void setReserve(double reserve) {
		this.reserve = reserve;
	}

	public double getOtherAmounts() {
		return otherAmounts;
	}

	public void setOtherAmounts(double otherAmounts) {
		this.otherAmounts = otherAmounts;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getEntity() {
		if (AonUtil.isEmpty(document)) {
			return null;
		}
		return DocumentUtil.isEntity(document)?"J":"F";
	}
	public String getProvinceStr() {
		String p = Integer.toString(province);
		return AonUtil.isEmpty(getDocument())?null:AonUtil.leftPad(p, 2, "0");
	}
	public String getRepresenStr() {
		return isRepresentative()?"1":"0"; 
	}
	
}
