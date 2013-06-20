package com.code.aon.file.tax.model.MOD123;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class Declaration {
	
	private String declarationType;
	private String administrationCode;
	private Integer year;
	private String period;
	private Integer currentDay;
	private Integer currentMonth;
	private String currentLetterMonth;
	private Integer currentYear;
	private boolean replacement;
	private boolean complementary;
	
	private String complementaryCode;
	private String replacedNumber;
	
	private boolean person;
	private String document;
	private Integer startPeriod;
	private Integer endPeriod;
	private Double result;
	private String name;
	private String surname;
	private String phone;
	
	private String streetInitial;
	private String streetName;
	private String streetNumber;
	private String streetStair;
	private String streetFloor;
	private String streetDoor;
	private String town;
	private String province;
	private String zip;

	private String contactPerson;
	private String contactPhone;
	private String contactCellular;
	private String contactMail;
	
	private String payment;
	private String ccc1;
	private String ccc2;
	private String ccc3;
	private String ccc4;
	
	private Map<String,Double> boxes = new HashMap<String, Double>();

	public String getAdministrationCode() {
		return administrationCode;
	}
	public void setAdministrationCode(String administrationCode) {
		this.administrationCode = administrationCode;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public Integer getCurrentDay() {
		return currentDay;
	}
	public void setCurrentDay(Integer currentDay) {
		this.currentDay = currentDay;
	}
	public Integer getCurrentMonth() {
		return currentMonth;
	}
	public void setCurrentMonth(Integer currentMonth) {
		this.currentMonth = currentMonth;
	}
	public String getCurrentLetterMonth() {
		return currentLetterMonth;
	}
	public void setCurrentLetterMonth(String currentLetterMonth) {
		this.currentLetterMonth = currentLetterMonth;
	}
	public Integer getCurrentYear() {
		return currentYear;
	}
	public void setCurrentYear(Integer currentYear) {
		this.currentYear = currentYear;
	}
	public boolean isReplacement() {
		return replacement;
	}
	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}
	public boolean isComplementary() {
		return complementary;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}
	public String getComplementaryCode() {
		return complementaryCode;
	}
	public void setComplementaryCode(String complementaryCode) {
		this.complementaryCode = complementaryCode;
	}
	public String getReplacedNumber() {
		return replacedNumber;
	}
	public void setReplacedNumber(String replacedNumber) {
		this.replacedNumber = replacedNumber;
	}
	public boolean isPerson() {
		return person;
	}
	public void setPerson(boolean person) {
		this.person = person;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

	public Integer getStartPeriod() {
		return startPeriod;
	}
	public void setStartPeriod(Integer startPeriod) {
		this.startPeriod = startPeriod;
	}

	public Integer getEndPeriod() {
		return endPeriod;
	}
	public void setEndPeriod(Integer endPeriod) {
		this.endPeriod = endPeriod;
	}

	public Double getResult() {
		return result;
	}
	public void setResult(Double result) {
		this.result = result;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getSurnameStart() {
		return StringUtils.substring(getSurname(), 0, 4);
	}
	public String getFullName() {
		return getSurname() + (StringUtils.isEmpty(getSurname())?"":' ') + getName();
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStreetInitial() {
		return streetInitial;
	}
	public void setStreetInitial(String streetInitial) {
		this.streetInitial = streetInitial;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String  streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getStreetStair() {
		return streetStair;
	}
	public void setStreetStair(String streetStair) {
		this.streetStair = streetStair;
	}
	public String getStreetFloor() {
		return streetFloor;
	}
	public void setStreetFloor(String streetFloor) {
		this.streetFloor = streetFloor;
	}
	public String getStreetDoor() {
		return streetDoor;
	}
	public void setStreetDoor(String streetDoor) {
		this.streetDoor = streetDoor;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getContactCellular() {
		return contactCellular;
	}
	public void setContactCellular(String contactCellular) {
		this.contactCellular = contactCellular;
	}
	public String getContactMail() {
		return contactMail;
	}
	public void setContactMail(String contactMail) {
		this.contactMail = contactMail;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getCcc1() {
		return ccc1;
	}
	public void setCcc1(String ccc1) {
		this.ccc1 = ccc1;
	}
	public String getCcc2() {
		return ccc2;
	}
	public void setCcc2(String ccc2) {
		this.ccc2 = ccc2;
	}
	public String getCcc3() {
		return ccc3;
	}
	public void setCcc3(String ccc3) {
		this.ccc3 = ccc3;
	}
	public String getCcc4() {
		return ccc4;
	}
	public void setCcc4(String ccc4) {
		this.ccc4 = ccc4;
	}
	public Map<String, Double> getBoxes() {
		return boxes;
	}
	public void setBoxes(Map<String, Double> boxes) {
		this.boxes = boxes;
	}
	public String getDeclarationType() {
		return declarationType;
	}
	public void setDeclarationType(String declarationType) {
		this.declarationType = declarationType;
	}
	@Override
	public String toString() {
		return getDocument() + " " + getName();
	}
}
