package com.code.aon.file.tax.model.MOD310.data;

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
	private String streetType;
	private String address;
	private Integer addressNumber;
	private String entity;
	private String city;
	private String province;
	private String provinceID;
	private Integer zip;
	private String telephone;
	private String fax;
	private String email;
	private String contactPerson;
	private String contactPhone;
	private String payment;
	private Double deposit;
	private String toDeduct;
	private String compensate;
	private String comments;
	private String ccc1;
	private String ccc2;
	private String ccc3;
	private String ccc4;
	
	private String epi1;
	private String epi2;
	private String epi3;
	private String epi4;
	private String epi5;
	private String agri1;
	private String agri2;
	private String agri3;
	private String agri4;
	
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
		if ( isPerson() ) {
			return StringUtils.substringAfter(name, " ");
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStreetType() {
		return streetType;
	}
	public void setStreetType(String streetType) {
		this.streetType = streetType;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getAddressNumber() {
		return addressNumber;
	}
	public void setAddressNumber(Integer addressNumber) {
		this.addressNumber = addressNumber;
	}

	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvinceID() {
		return provinceID;
	}
	public void setProvinceID(String provinceID) {
		this.provinceID = provinceID;
	}
	public Integer getZip() {
		return zip;
	}
	public void setZip(Integer zip) {
		this.zip = zip;
	}

	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getCompensate() {
		return compensate;
	}
	public void setCompensate(String compensate) {
		this.compensate = compensate;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
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
	public String getAgri1() {
		return agri1;
	}
	public void setAgri1(String agri1) {
		this.agri1 = agri1;
	}
	public String getAgri2() {
		return agri2;
	}
	public void setAgri2(String agri2) {
		this.agri2 = agri2;
	}
	public String getAgri3() {
		return agri3;
	}
	public void setAgri3(String agri3) {
		this.agri3 = agri3;
	}
	public String getAgri4() {
		return agri4;
	}
	public void setAgri4(String agri4) {
		this.agri4 = agri4;
	}
	public Map<String, Double> getBoxes() {
		return boxes;
	}
	public void setBoxes(Map<String, Double> boxes) {
		this.boxes = boxes;
	}
	public String getSurnameStart() {
		return StringUtils.substring(getName(), 0, 4);
	}
	public String getOnlyName() {
		if ( isPerson() ) {
			return StringUtils.substringBefore(name, " ");
		}
		return null;
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
