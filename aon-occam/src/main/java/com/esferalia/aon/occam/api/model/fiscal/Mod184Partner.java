package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonNumberUtils;

public class Mod184Partner implements Serializable {

	private static final long serialVersionUID = -7095816615909233361L;
	
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
	
	private double expenses;
	private String nature;
	private String location;
	private String cadasdralReference;
	private String declaredKey;
	private double assetPercent;
	private int assetDays;
	
	private double rendNetoPrevio;
	private double rendNetoMinorado;
	

	private boolean dirty;
	private boolean deleted;
	private int tempId;	

	public Integer getId() {
		return id;
	}

	public Mod184Partner setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod184Partner setName(String name) {
		this.name = name;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod184Partner setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod184() {
		return mod184;
	}

	public Mod184Partner setMod184(int mod184) {
		this.mod184 = mod184;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod184Partner setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public Mod184Partner setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
		return this;
	}

	public int getProvince() {
		return province;
	}

	public Mod184Partner setProvince(int province) {
		this.province = province;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public Mod184Partner setCountry(String country) {
		this.country = country;
		return this;
	}

	public byte getPartType() {
		return partType;
	}

	public Mod184Partner setPartType(byte partType) {
		this.partType = partType;
		return this;
	}

	public boolean isMemberEndOfYear() {
		return memberEndOfYear;
	}

	public Mod184Partner setMemberEndOfYear(boolean memberEndOfYear) {
		this.memberEndOfYear = memberEndOfYear;
		return this;
	}

	public Integer getMemberDays() {
		return memberDays;
	}

	public Mod184Partner setMemberDays(Integer memberDays) {
		this.memberDays = memberDays;
		return this;
	}

	public double getPartPercent() {
		return partPercent;
	}

	public Mod184Partner setPartPercent(double partPercent) {
		this.partPercent = partPercent;
		return this;
	}

	public String getKey() {
		return key;
	}

	public Mod184Partner setKey(String key) {
		this.key = key;
		return this;
	}

	public String getSubKey() {
		return subKey;
	}

	public Mod184Partner setSubKey(String subKey) {
		this.subKey = subKey;
		return this;
	}

	public double getAmount() {
		return amount;
	}

	public Mod184Partner setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public double getReduction() {
		return reduction;
	}

	public Mod184Partner setReduction(double reduction) {
		this.reduction = reduction;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public Mod184Partner setAddress(String address) {
		this.address = address;
		return this;
	}

	public double getExpenses() {
		return expenses;
	}
	public Mod184Partner setExpenses(double expenses) {
		this.expenses = expenses;
		return this;
	}
	public String getNature() {
		return nature;
	}
	public int getNatureIndex() {
		try {
			return nature==null?0:AonNumberUtils.toint(nature);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	public Mod184Partner setNature(String nature) {
		this.nature = nature;
		return this;
	}
	public String getLocation() {
		return location;
	}
	public Mod184Partner setLocation(String location) {
		this.location = location;
		return this;
	}
	public String getCadasdralReference() {
		return cadasdralReference;
	}

	public Mod184Partner setCadasdralReference(String cadasdralReference) {
		this.cadasdralReference = cadasdralReference;
		return this;
	}
	
	public String getDeclaredKey() {
		return declaredKey;
	}
	public Mod184Partner setDeclaredKey(String declaredKey) {
		this.declaredKey = declaredKey;
		return this;
	}
	public double getAssetPercent() {
		return assetPercent;
	}
	public Mod184Partner setAssetPercent(double assetPercent) {
		this.assetPercent = assetPercent;
		return this;
	}
	
	public int getAssetDays() {
		return assetDays;
	}
	public Mod184Partner setAssetDays(int assetDays) {
		this.assetDays = assetDays;
		return this;
	}

	public double getRendNetoPrevio() {
		return rendNetoPrevio;
	}
	public Mod184Partner setRendNetoPrevio(double rendNetoPrevio) {
		this.rendNetoPrevio = rendNetoPrevio;
		return this;
	}

	public double getRendNetoMinorado() {
		return rendNetoMinorado;
	}
	public Mod184Partner setRendNetoMinorado(double rendNetoMinorado) {
		this.rendNetoMinorado = rendNetoMinorado;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Mod184Partner setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod184Partner setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public int getTempId() {
		return tempId;
	}

	public Mod184Partner setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}
}
