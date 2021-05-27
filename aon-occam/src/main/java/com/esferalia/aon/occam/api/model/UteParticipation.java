package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class UteParticipation implements Serializable {

	private static final long serialVersionUID = 3400183583793598978L;
	
	private String document;
	private boolean representative;
	private String name;
	private int province;
	private String country;
	private double base;
	private double percent;
	

	public String getDocument() {
		return document;
	}

	public UteParticipation setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public UteParticipation setName(String name) {
		this.name = name;
		return this;
	}

	public int getProvince() {
		return province;
	}

	public UteParticipation setProvince(int province) {
		this.province = province;
		return this;
	}
	public String getCountry() {
		return country;
	}

	public UteParticipation setCountry(String country) {
		this.country = country;
		return this;
	}

	public boolean isRepresentative() {
		return representative;
	}
	public UteParticipation setRepresentative(boolean representative) {
		this.representative = representative;
		return this;
	}

	public double getPercent() {
		return percent;
	}

	public UteParticipation setPercent(double percent) {
		this.percent = percent;
		return this;
	}

	public double getBase() {
		return base;
	}

	public UteParticipation setBase(double base) {
		this.base = base;
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
