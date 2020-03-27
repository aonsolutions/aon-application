package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Country;

public class EmployeeInfo implements Serializable{
	
	private Integer domain;
	private Integer contractId;
	private Boolean isContractActive;
	
	private Integer employeeId; //Registry Id
	
	//Person Table
	private Date birthdate;
	private Byte gender;
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
	private Integer geozoneId;
	private String addressProvinces;
	
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
	
	//Rbanks 
	private ArrayList<Rbank> rbanks;
	
	public EmployeeInfo() {
		super();
		this.domain = null;
		this.contractId = null;
		this.isContractActive = null;
		this.employeeId = null;
		this.birthdate = null;
		this.gender = null;
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
		this.geozoneId = null;
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
		this.rbanks = new ArrayList<Rbank>();
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
		return nationality != null ? Country.valueOf(nationality).getName() : "";
	}
	
	public String getNationalityCode() {
		return nationality;
	}

	public void setNationality(String nationality) {
		if(null == nationality)
			this.nationality = "ES";
		else
			this.nationality = nationality;
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
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Byte getGender() {
		return (null == gender ? (byte)2 : gender) ;
	}

	public void setGender(Byte gender) {
		this.gender = gender;
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

	public String getAddressProvinces() {
		return addressProvinces;
	}

	public void setAddressProvinces(String addressProvinces) {
		this.addressProvinces = addressProvinces;
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

	public void setBic(String bic) {
		this.bic = bic;
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

	public Integer getGeozoneId() {
		return geozoneId;
	}

	public void setGeozoneId(Integer geozoneId) {
		this.geozoneId = geozoneId;
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
	
	public void addRbank(Integer id, String iban, String bic) {
		this.rbanks.add(new Rbank(id, iban, bic));
	}
	
	public ArrayList<Rbank> getRbanks() {
		return rbanks;
	}

	public String toString(){
		String result = "";
		
		result += "---------------- (NEW) EMPLOYEE INFO ---------------- \n";
		result += " **** Person Table **** \n";
		result += "Registry Id : " + employeeId + "\n";
		result += "Birth Date : " + birthdate + "\n";
		result += "Gender : " + gender + "\n";
		result += "SS Number : " + ssNumber + "\n";
		result += "Name : " + name + "\n";
		result += "Surname : " + surName + "\n";
		result += "Second Surname : " + secondSurName + "\n";
		result += " **** Registry Table **** \n";
		result += "Document : " + document + "\n";
		result += "Document Type : " + documentType + "\n";
		result += "Nationality : " + nationality + "\n";
		result += " **** Raddress Table **** \n";
		result += "Raddress Id : " + raddressId + "\n";
		result += "Street Type : " + streetType + "\n";
		result += "Address : " + address + "\n";
		result += "Address Number : " + addresNum + "\n";
		result += "Address Zip : " + addressZip + "\n";
		result += "Address City : " + addressCity + "\n";
		result += " **** Geozone Table **** \n";
		result += "Geozone Id : " + geozoneId + "\n";
		result += "Province : " + addressProvinces + "\n";
		result += " **** Rmedia Table **** \n";
		result += "Mobile Id : " + mobileId + "\n";
		result += "Mobile : " + mobile + "\n";
		result += "Phone Id : " + phoneId + "\n";
		result += "Phone : "+ phone + "\n";
		result += "Email Id : " + emailId + "\n";
		result += "Emai : " + email + "\n";
		result += " **** RPayMethod Table **** \n";
		result += "RPayMethod Id : " + rpaymethodId + "\n";
		result += " **** PayMethod Table **** \n";
		result += "Pay Method Id : " + paymethodId + "\n";
		result += "Pay Method : " + payMethodTypeB + "\n";
		result += " **** RBank Table **** \n";
		result += "RBank Id : " + rbankId + "\n";
		result += "Account : " + account + "\n";
		result += "Bic : " + bic + "\n";
		
		return result;
		
	}
}