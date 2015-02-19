package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Mod184Partner implements Serializable {

	private Integer id;
	private int domain;
	private int mod184;
	private String document;
	private String representativeDocument;
	private String name;
	private int province;
	private String country;
	private byte partType;
	private boolean memberEndOfYear;
	private Integer memberDays;
	private double partPercent;
	private String key;
	private String subKey;
	private double amount;
	private double reduction;
	private String address;

	private boolean dirty;
	private boolean deleted;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public byte getPartType() {
		return partType;
	}

	public void setPartType(byte partType) {
		this.partType = partType;
	}

	public boolean isMemberEndOfYear() {
		return memberEndOfYear;
	}

	public void setMemberEndOfYear(boolean memberEndOfYear) {
		this.memberEndOfYear = memberEndOfYear;
	}

	public Integer getMemberDays() {
		return memberDays;
	}

	public void setMemberDays(Integer memberDays) {
		this.memberDays = memberDays;
	}

	public double getPartPercent() {
		return partPercent;
	}

	public void setPartPercent(double partPercent) {
		this.partPercent = partPercent;
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getReduction() {
		return reduction;
	}

	public void setReduction(double reduction) {
		this.reduction = reduction;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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
