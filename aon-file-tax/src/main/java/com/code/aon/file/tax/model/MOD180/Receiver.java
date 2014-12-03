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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCadasdralReference() {
		return cadasdralReference;
	}

	public void setCadasdralReference(String cadasdralReference) {
		this.cadasdralReference = cadasdralReference;
	}

	public String getStreetType() {
		return streetType;
	}

	public void setStreetType(String streetType) {
		this.streetType = streetType;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getNumberType() {
		return numberType;
	}

	public void setNumberType(String numberType) {
		this.numberType = numberType;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumberSuffix() {
		return numberSuffix;
	}

	public void setNumberSuffix(String numberSuffix) {
		this.numberSuffix = numberSuffix;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getHall() {
		return hall;
	}

	public void setHall(String hall) {
		this.hall = hall;
	}

	public String getStair() {
		return stair;
	}

	public void setStair(String stair) {
		this.stair = stair;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getDoor() {
		return door;
	}

	public void setDoor(String door) {
		this.door = door;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getTownCode() {
		return townCode;
	}

	public void setTownCode(String townCode) {
		this.townCode = townCode;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	
}
