package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CompanyAdministrator implements Serializable, IsSerializable {
	
	private static final long serialVersionUID = 1439647684878998653L;
	
	private String document;
	private String name;
	private boolean shareholder;
	private boolean representative;
	private boolean administrator;
	private double percent;
	private double nominalValue;
	private String residence;
	private int province;
	
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
	public boolean isShareholder() {
		return shareholder;
	}
	public void setShareholder(boolean shareholder) {
		this.shareholder = shareholder;
	}
	public boolean isRepresentative() {
		return representative;
	}
	public void setRepresentative(boolean representative) {
		this.representative = representative;
	}
	public boolean isAdministrator() {
		return administrator;
	}
	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
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
	public String getResidence() {
		return residence;
	}
	public void setResidence(String residence) {
		this.residence = residence;
	}
	public int getProvince() {
		return province;
	}
	public void setProvince(int province) {
		this.province = province;
	}
}
