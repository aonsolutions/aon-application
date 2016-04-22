package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CompanyAdministrator implements Serializable {
	
	private static final long serialVersionUID = 2561834391482286106L;
	
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
	public CompanyAdministrator setDocument(String document) {
		this.document = document;
		return this;
	}
	public String getName() {
		return name;
	}
	public CompanyAdministrator setName(String name) {
		this.name = name;
		return this;
	}
	public boolean isShareholder() {
		return shareholder;
	}
	public CompanyAdministrator setShareholder(boolean shareholder) {
		this.shareholder = shareholder;
		return this;
	}
	public boolean isRepresentative() {
		return representative;
	}
	public CompanyAdministrator setRepresentative(boolean representative) {
		this.representative = representative;
		return this;
	}
	public boolean isAdministrator() {
		return administrator;
	}
	public CompanyAdministrator setAdministrator(boolean administrator) {
		this.administrator = administrator;
		return this;
	}
	public double getPercent() {
		return percent;
	}
	public CompanyAdministrator setPercent(double percent) {
		this.percent = percent;
		return this;
	}
	public double getNominalValue() {
		return nominalValue;
	}
	public CompanyAdministrator setNominalValue(double nominalValue) {
		this.nominalValue = nominalValue;
		return this;
	}
	public String getResidence() {
		return residence;
	}
	public CompanyAdministrator setResidence(String residence) {
		this.residence = residence;
		return this;
	}
	public int getProvince() {
		return province;
	}
	public CompanyAdministrator setProvince(int province) {
		this.province = province;
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
