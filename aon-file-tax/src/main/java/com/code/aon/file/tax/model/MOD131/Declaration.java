package com.code.aon.file.tax.model.MOD131;

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
	private Double deposit;
	private String toDeduct;
	private String ccc;
	private String iban;
	
	private String epi1;
	private String epi2;
	private String epi3;
	private String epi4;
	private String epi5;
	
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
		return isComplementary()?"X":"";
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
	public Double getDeposit() {
		return deposit;
	}
	public void setDeposit(Double deposit) {
		this.deposit = deposit;
	}
	public String getToDeduct() {
		return toDeduct;
	}
	public void setToDeduct(String toDeduct) {
		this.toDeduct = toDeduct;
	}
	public String getCcc() {
		return ccc;
	}
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public String getEpi1() {
		return epi1;
	}
	public void setEpi1(String epi1) {
		this.epi1 = epi1;
	}
	public String getEpi2() {
		return epi2;
	}
	public void setEpi2(String epi2) {
		this.epi2 = epi2;
	}
	public String getEpi3() {
		return epi3;
	}
	public void setEpi3(String epi3) {
		this.epi3 = epi3;
	}
	public String getEpi4() {
		return epi4;
	}
	public void setEpi4(String epi4) {
		this.epi4 = epi4;
	}
	public String getEpi5() {
		return epi5;
	}
	public void setEpi5(String epi5) {
		this.epi5 = epi5;
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

	public void changeInvalidCharacters() {
		setAdministrationCode(changeInvalidCharacters(getAdministrationCode()));
		setCurrentLetterMonth(changeInvalidCharacters(getCurrentLetterMonth()));
		setReplacedNumber(changeInvalidCharacters(getReplacedNumber()));
		setDocument(changeInvalidCharacters(getDocument()));
		setName(changeInvalidCharacters(getName()));
		setSurname(changeInvalidCharacters(getSurname()));
		setPhone(changeInvalidCharacters(getPhone()));
		setStreetInitial(changeInvalidCharacters(getStreetInitial()));
		setStreetName(changeInvalidCharacters(getStreetName()));
		setStreetNumber(changeInvalidCharacters(getStreetNumber()));
		setStreetStair(changeInvalidCharacters(getStreetStair()));
		setStreetFloor(changeInvalidCharacters(getStreetFloor()));
		setStreetDoor(changeInvalidCharacters(getStreetDoor()));
		setTown(changeInvalidCharacters(getTown()));
		setProvince(changeInvalidCharacters(getProvince()));
		setZip(changeInvalidCharacters(getZip()));
		setContactPerson(changeInvalidCharacters(getContactPerson()));
		setContactPhone(changeInvalidCharacters(getContactPhone()));
		setContactCellular(changeInvalidCharacters(getContactCellular()));
		setContactMail(changeInvalidCharacters(getContactMail()));
		setPayment(changeInvalidCharacters(getPayment()));
		setToDeduct(changeInvalidCharacters(getToDeduct()));
	}
	
	public String changeInvalidCharacters(String token) {
		char[] seek  = new char[]{'á','é','í','ó','ú','Á','É','Í','Ó','Ú','º','ª'};
		char[] alter = new char[]{'a','e','i','o','u','A','E','I','O','U',' ',' '};
		if (StringUtils.isNotBlank(token)) {
			for (int i = 0; i < seek.length ; i ++) {
				token = StringUtils.replaceChars(token, seek[i], alter[i]);
			}
		}
		return token;
	}
}
