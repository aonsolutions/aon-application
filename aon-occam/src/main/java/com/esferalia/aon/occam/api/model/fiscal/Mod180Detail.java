package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod180Detail implements Serializable {

	private static final long serialVersionUID = 5848842525467449143L;
	
	private Integer id;
	private String name;
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
	
	private String location;
	private String cadasdralReference;
	private String streetType;
	private String streetName;
	private String numberType;
	private String number;
	private String numberSuffix;
	private String block;
	private String hall;
	private String stair;
	private String floor;
	private String door;
	private String complement;
	private String city;
	private String town;
	private String townCode;
	private String provinceCode;
	private String zip;

	private boolean dirty;
	private boolean deleted;

	public Integer getId() {
		return id;
	}

	public Mod180Detail setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod180Detail setName(String name) {
		this.name = name;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod180Detail setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod180() {
		return mod180;
	}

	public Mod180Detail setMod180(int mod180) {
		this.mod180 = mod180;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod180Detail setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public Mod180Detail setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
		return this;
	}

	public int getProvince() {
		return province;
	}

	public Mod180Detail setProvince(int province) {
		this.province = province;
		return this;
	}

	public double getPerception() {
		return perception;
	}

	public Mod180Detail setPerception(double perception) {
		this.perception = perception;
		return this;
	}

	public double getRetention() {
		return retention;
	}

	public Mod180Detail setRetention(double retention) {
		this.retention = retention;
		return this;
	}

	public int getAccrualYear() {
		return accrualYear;
	}

	public Mod180Detail setAccrualYear(int accrualYear) {
		this.accrualYear = accrualYear;
		return this;
	}

	public boolean isInKind() {
		return inKind;
	}

	public Mod180Detail setInKind(boolean inKind) {
		this.inKind = inKind;
		return this;
	}

	public double getPercent() {
		return percent;
	}

	public Mod180Detail setPercent(double percent) {
		this.percent = percent;
		return this;
	}
	
	public String getLocation() {
		return location;
	}

	public Mod180Detail setLocation(String location) {
		this.location = location;
		return this;
	}

	public String getCadasdralReference() {
		return cadasdralReference;
	}

	public Mod180Detail setCadasdralReference(String cadasdralReference) {
		this.cadasdralReference = cadasdralReference;
		return this;
	}

	public String getStreetType() {
		return streetType;
	}

	public Mod180Detail setStreetType(String streetType) {
		this.streetType = streetType;
		return this;
	}

	public String getStreetName() {
		return streetName;
	}

	public Mod180Detail setStreetName(String streetName) {
		this.streetName = streetName;
		return this;
	}

	public String getNumberType() {
		return numberType;
	}

	public Mod180Detail setNumberType(String numberType) {
		this.numberType = numberType;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public Mod180Detail setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getNumberSuffix() {
		return numberSuffix;
	}

	public Mod180Detail setNumberSuffix(String numberSuffix) {
		this.numberSuffix = numberSuffix;
		return this;
	}

	public String getBlock() {
		return block;
	}

	public Mod180Detail setBlock(String block) {
		this.block = block;
		return this;
	}

	public String getHall() {
		return hall;
	}

	public Mod180Detail setHall(String hall) {
		this.hall = hall;
		return this;
	}

	public String getStair() {
		return stair;
	}

	public Mod180Detail setStair(String stair) {
		this.stair = stair;
		return this;
	}

	public String getFloor() {
		return floor;
	}

	public Mod180Detail setFloor(String floor) {
		this.floor = floor;
		return this;
	}

	public String getDoor() {
		return door;
	}

	public Mod180Detail setDoor(String door) {
		this.door = door;
		return this;
	}

	public String getComplement() {
		return complement;
	}

	public Mod180Detail setComplement(String complement) {
		this.complement = complement;
		return this;
	}

	public String getCity() {
		return city;
	}

	public Mod180Detail setCity(String city) {
		this.city = city;
		return this;
	}

	public String getTown() {
		return town;
	}

	public Mod180Detail setTown(String town) {
		this.town = town;
		return this;
	}

	public String getTownCode() {
		return townCode;
	}

	public Mod180Detail setTownCode(String townCode) {
		this.townCode = townCode;
		return this;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public Mod180Detail setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
		return this;
	}

	public String getZip() {
		return zip;
	}

	public Mod180Detail setZip(String zip) {
		this.zip = zip;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Mod180Detail setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod180Detail setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

}
