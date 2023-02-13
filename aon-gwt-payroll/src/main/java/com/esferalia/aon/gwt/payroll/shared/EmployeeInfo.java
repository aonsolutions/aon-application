package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EmployeeInfo implements Serializable{
	
	private Integer domain;
	private Integer contractId;
	private Boolean isContractActive;
	private Boolean isFullTime;
	
	private Integer employeeId; //Registry Id
	
	//Person Table
	private String birthdate;
	private Byte gender;
	private Byte civilStatus;
	private String ssNumber;
	private String name;
	private String surName;
	private String secondSurName;
	
	//Registry Table
	private String document;
	private Byte documentType;
	private String nationality;
	
	//Raddress
	private Integer raddressId;
	private String streetType;
	private String address;
	private String addresNum;
	private String addressInfo;
	private String addressZip;
	private String addressCity;
	
	//Geozone
	private Integer addressProvinces;
	
	//Rmedia
	private Integer mobileId;
	private String mobile;
	private Integer phoneId;
	private String phone;
	private Integer emailId;
	private String email;
	
	//Rpaymethod
	private Integer rpaymethodId;
	
	//Paymethod
	private Integer paymethodId;
	private String payMethodType;
	private byte payMethodTypeB;
	
	//Rbank
	private Integer rbankId;
	private String account;
	private String bic;
	private String bankAlias;
	
	//Rbanks 
	private ArrayList<Rbank> rbanks;
	
	//AgreementId
	private Integer agreementId;
	
	public EmployeeInfo() {
		super();
		this.domain = null;
		this.contractId = null;
		this.isContractActive = null;
		this.employeeId = null;
		this.birthdate = null;
		this.gender = null;
		this.civilStatus = null;
		this.ssNumber = null;
		this.name = null;
		this.surName = null;
		this.secondSurName = null;
		this.document = null;
		this.documentType = null;
		this.nationality = null;
		this.raddressId = null;
		this.streetType = null;
		this.address = null;
		this.addresNum = null;
		this.addressZip = null;
		this.addressInfo = null;
		this.addressCity = null;
		this.addressProvinces = null;
		this.mobileId = null;
		this.mobile = null;
		this.phoneId = null;
		this.phone = null;
		this.emailId = null;
		this.email = null;
		this.rpaymethodId = null;
		this.paymethodId = null;
		this.payMethodType = null;
		this.rbankId = null;
		this.account = null;
		this.bic = null;
		this.bankAlias = null;
		this.rbanks = new ArrayList<Rbank>();
		this.agreementId = null;
	}

	public EmployeeInfo(Integer employeeId, String name, String surName, String document, String ssNumber) {
		super();
		this.employeeId = employeeId;
		this.name = name;
		this.surName = surName;
		this.document = document;
		this.ssNumber = ssNumber;
	}

	// ------------- GETTERS / SETTERS -------------
	
	public Integer getEmployeeId() {
		return employeeId;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public Integer getContractId() {
		return contractId;
	}
	
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}
	
	public Boolean getContractActive() {
		return this.isContractActive;
	}
	
	public void setContractActive(Boolean isActive) {
		this.isContractActive = isActive;
	}

	public String getName() {
		return name;
	}

	public String getSurName() {
		return null == surName ? "" : surName;
	}

	public String getDocument() {
		return document;
	}

	public String getSsNumber() {
		return ssNumber;
	}

	public String getNationality() {
		return AonStringUtils.isNotBlank(nationality) ? getCountryName(nationality) : "";
	}
	
	private String getCountryName(String iso2) {
		for(Country country : Country.values())
			if(AonStringUtils.equalsIgnoreCase(country.getIso2(), iso2))
					return country.getName();
		
		return "";
	}

	public String getNationalityCode() {
		return nationality;
	}

	public void setNationality(String nationalityIso2) {
		this.nationality = AonStringUtils.isBlank(nationalityIso2) ? "ES" : nationalityIso2;
	}

	public Byte getDocumentType() {
		return documentType;
	}

	public void setDocumentType(Byte documentType) {
		this.documentType = documentType;
	}

	public String getSecondSurName() {
		return null == secondSurName ? "" : secondSurName;
	}

	public void setSecondSurName(String secondSurName) {
		this.secondSurName = secondSurName;
	}

	public Date getBirthdate() {
		return Shared.parse(birthdate);
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = Shared.format(birthdate);
	}

	public Byte getGender() {
		return (null == gender ? (byte)2 : gender) ;
	}

	public void setGender(Byte gender) {
		this.gender = gender;
	}
	
	public Byte getCivilStatus() {
		return (null == civilStatus ? (byte)0 : civilStatus) ;
	}
	
	public void setCivilStatus(Byte civilStatus) {
		this.civilStatus = civilStatus;
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

	public String getAddresNum() {
		return addresNum;
	}

	public void setAddresNum(String addresNum) {
		this.addresNum = addresNum;
	}

	public String getAddressZip() {
		return addressZip;
	}

	public void setAddressZip(String addressZip) {
		this.addressZip = addressZip;
	}

	public String getAddressInfo() {
		return addressInfo;
	}

	public void setAddressInfo(String addressInfo) {
		this.addressInfo = addressInfo;
	}

	public String getAddressCity() {
		return addressCity;
	}

	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}

	public Integer getAddressProvinces() {
		return addressProvinces;
	}

	public void setAddressProvinces(Integer geozoneId) {
		this.addressProvinces = geozoneId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPayMethodType() {
		return payMethodType;
	}

	public void setPayMethodType(String payMethodType) {
		this.payMethodType = payMethodType;
	}
	
	public byte getPayMethodTypeB() {
		return payMethodTypeB;
	}

	public void setPayMethodTypeB(byte payMethodType) {
		this.payMethodTypeB = payMethodType;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getBic() {
		return bic;
	}
	
	public String getBankAlias() {
		return bankAlias;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}
	
	public void setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setSsNumber(String ssNumber) {
		this.ssNumber = ssNumber;
	}

	public Integer getRaddressId() {
		return raddressId;
	}

	public void setRaddressId(Integer raddressId) {
		this.raddressId = raddressId;
	}

	public Integer getMobileId() {
		return mobileId;
	}

	public void setMobileId(Integer mobileId) {
		this.mobileId = mobileId;
	}

	public Integer getPhoneId() {
		return phoneId;
	}

	public void setPhoneId(Integer phoneId) {
		this.phoneId = phoneId;
	}

	public Integer getEmailId() {
		return emailId;
	}

	public void setEmailId(Integer emailId) {
		this.emailId = emailId;
	}

	public Integer getRpaymethodId() {
		return rpaymethodId;
	}

	public void setRpaymethodId(Integer rpaymethodId) {
		this.rpaymethodId = rpaymethodId;
	}

	public Integer getPaymethodId() {
		return paymethodId;
	}

	public void setPaymethodId(Integer paymethodId) {
		this.paymethodId = paymethodId;
	}

	public Integer getRbankId() {
		return rbankId;
	}

	public void setRbankId(Integer rbankId) {
		this.rbankId = rbankId;
	}
	
	public String getFullName() {
		return (null == getSurName() ? "" :  getSurName() + " ") + 
		(null == getSecondSurName() ? "" : getSecondSurName() + ", ") + 
		getName();
	}
	
	public void addRbank(Integer id, String iban, String bic, String bankAlias) {
		this.rbanks.add(new Rbank(id, iban, bic, bankAlias));
	}
	
	public ArrayList<Rbank> getRbanks() {
		return rbanks;
	}

	public Boolean getIsFullTime() {
		return isFullTime;
	}

	public void setIsFullTime(Boolean isFullTime) {
		this.isFullTime = isFullTime;
	}
	
	public Integer getAgreementId() {
		return this.agreementId;
	}
	
	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	@Override
	public String toString() {
		return "EmployeeInfo [domain=" + domain + ", contractId=" + contractId + ", isContractActive="
				+ isContractActive + ", isFullTime=" + isFullTime + ", employeeId=" + employeeId + ", birthdate="
				+ birthdate + ", gender=" + gender + ", civilStatus=" + civilStatus + ", ssNumber=" + ssNumber
				+ ", name=" + name + ", surName=" + surName + ", secondSurName=" + secondSurName + ", document="
				+ document + ", documentType=" + documentType + ", nationality=" + nationality + ", raddressId="
				+ raddressId + ", streetType=" + streetType + ", address=" + address + ", addresNum=" + addresNum
				+ ", addressInfo=" + addressInfo + ", addressZip=" + addressZip + ", addressCity=" + addressCity
				+ ", addressProvinces=" + addressProvinces + ", mobileId=" + mobileId
				+ ", mobile=" + mobile + ", phoneId=" + phoneId + ", phone=" + phone + ", emailId=" + emailId
				+ ", email=" + email + ", rpaymethodId=" + rpaymethodId + ", paymethodId=" + paymethodId
				+ ", payMethodType=" + payMethodType + ", payMethodTypeB=" + payMethodTypeB + ", rbankId=" + rbankId
				+ ", account=" + account + ", bic=" + bic + ", bankAlias=" + bankAlias + ", rbanks=" + rbanks
				+ ", agreementId=" + agreementId + "]";
	}

}