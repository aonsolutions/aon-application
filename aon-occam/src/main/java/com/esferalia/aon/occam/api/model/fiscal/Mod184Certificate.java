package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Mod184Certificate implements Serializable {

	private static final long serialVersionUID = 6825479857151023190L;
	
	private int year;
	private String entityDocument;
	private String entityName;
	private String memberDocument;
	private String memberName;
	private String address; 
	private int province;
	private String country;
	private byte partType;
	private boolean memberEndOfYear;
	private Integer memberDays;
	private double partPercent;
	private Map<String,double[]> detail; // Clave+Subclave, [importe,reducción]
	
	public int getYear() {
		return year;
	}

	public Mod184Certificate setYear(int year) {
		this.year = year;
		return this;
	}

	public String getEntityDocument() {
		return entityDocument;
	}

	public Mod184Certificate setEntityDocument(String entityDocument) {
		this.entityDocument = entityDocument;
		return this;
	}

	public String getEntityName() {
		return entityName;
	}

	public Mod184Certificate setEntityName(String entityName) {
		this.entityName = entityName;
		return this;
	}
	
	public String getMemberDocument() {
		return memberDocument;
	}

	public Mod184Certificate setMemberDocument(String document) {
		this.memberDocument = document;
		return this;
	}

	public String getMemberName() {
		return memberName;
	}

	public Mod184Certificate setMemberName(String name) {
		this.memberName = name;
		return this;
	}
	
	public String getAddress() {
		return address;
	}

	public Mod184Certificate setAddress(String address) {
		this.address = address;
		return this;
	}

	public int getProvince() {
		return province;
	}

	public Mod184Certificate setProvince(int province) {
		this.province = province;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public Mod184Certificate setCountry(String country) {
		this.country = country;
		return this;
	}

	public byte getPartType() {
		return partType;
	}

	public Mod184Certificate setPartType(byte partType) {
		this.partType = partType;
		return this;
	}

	public boolean isMemberEndOfYear() {
		return memberEndOfYear;
	}

	public Mod184Certificate setMemberEndOfYear(boolean memberEndOfYear) {
		this.memberEndOfYear = memberEndOfYear;
		return this;
	}

	public Integer getMemberDays() {
		return memberDays;
	}

	public Mod184Certificate setMemberDays(Integer memberDays) {
		this.memberDays = memberDays;
		return this;
	}

	public double getPartPercent() {
		return partPercent;
	}

	public Mod184Certificate setPartPercent(double partPercent) {
		this.partPercent = partPercent;
		return this;
	}

	public Map<String,double[]> getDetail() {
		if (detail==null)
			detail = new HashMap<>();
		return detail;
	}

}
