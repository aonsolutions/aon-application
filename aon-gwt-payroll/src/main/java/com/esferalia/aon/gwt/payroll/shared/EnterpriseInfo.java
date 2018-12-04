package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class EnterpriseInfo implements Serializable {
	
	//Enterprise table
	private Integer scopeId;
	private Integer calendarId;
	private Integer domainId;
	
	//Registry table
	private Integer enterpriseId; //Registry
	private String name;
	private String alias;
	private Byte documentType;
	private String documentCountry;
	private String document;
	
	//Raddress table
	private Integer raddressId;
	private String streetType;
	private String address;
	private String addressNum;
	private String addressZip;
	private String addressCity;
	
	//Geozone table
	private Integer geozoneId;
	private String addressProvince;
	
	//Rmedia table
	private Integer mobileId;
	private String mobile;
	private Integer phoneId;
	private String phone;
	private Integer emailId;
	private String email;
	private Integer webId;
	private String web;
	
	//Enterprise date table
	private Integer paysheetModelId;
	private Byte paysheetModel;
	private Integer costsModelId;
	private Byte costsModel;
	private Integer paysheetSendTypeId;
	private Byte paysheetSendType;
	private Integer paysheetEmailId;
	private String paysheetEmail;
	private Integer enterpriseAgreementId;
	private String enterpriseAgreement;
	
	//Rattach
	String signature;
	String logo;
	
	public EnterpriseInfo() {
		super();
	}

	public Integer getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Byte getDocumentType() {
		return documentType;
	}

	public void setDocumentType(Byte documentType) {
		this.documentType = documentType;
	}

	public String getDocumentCountry() {
		return documentCountry;
	}

	public void setDocumentCountry(String documentCountry) {
		this.documentCountry = documentCountry;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public Integer getRaddressId() {
		return raddressId;
	}

	public void setRaddressId(Integer raddressId) {
		this.raddressId = raddressId;
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

	public String getAddressNum() {
		return addressNum;
	}

	public void setAddressNum(String addressNum) {
		this.addressNum = addressNum;
	}

	public String getAddressZip() {
		return addressZip;
	}

	public void setAddressZip(String addressZip) {
		this.addressZip = addressZip;
	}

	public String getAddressCity() {
		return addressCity;
	}

	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}

	public Integer getGeozoneId() {
		return geozoneId;
	}

	public void setGeozoneId(Integer geozoneId) {
		this.geozoneId = geozoneId;
	}

	public String getAddressProvince() {
		return addressProvince;
	}

	public void setAddressProvince(String addressProvince) {
		this.addressProvince = addressProvince;
	}

	public Integer getMobileId() {
		return mobileId;
	}

	public void setMobileId(Integer mobileId) {
		this.mobileId = mobileId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getPhoneId() {
		return phoneId;
	}

	public void setPhoneId(Integer phoneId) {
		this.phoneId = phoneId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getEmailId() {
		return emailId;
	}

	public void setEmailId(Integer emailId) {
		this.emailId = emailId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getWebId() {
		return webId;
	}

	public void setWebId(Integer webId) {
		this.webId = webId;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public Integer getScopeId() {
		return scopeId;
	}

	public void setScopeId(Integer scopeId) {
		this.scopeId = scopeId;
	}

	public Integer getPaysheetModelId() {
		return paysheetModelId;
	}

	public void setPaysheetModelId(Integer paysheetModelId) {
		this.paysheetModelId = paysheetModelId;
	}

	public Byte getPaysheetModel() {
		return paysheetModel;
	}

	public void setPaysheetModel(Byte paysheetModel) {
		this.paysheetModel = paysheetModel;
	}

	public Integer getCostsModelId() {
		return costsModelId;
	}

	public void setCostsModelId(Integer costsModelId) {
		this.costsModelId = costsModelId;
	}

	public Byte getCostsModel() {
		return costsModel;
	}

	public void setCostsModel(Byte costsModel) {
		this.costsModel = costsModel;
	}

	public Integer getPaysheetSendTypeId() {
		return paysheetSendTypeId;
	}

	public void setPaysheetSendTypeId(Integer paysheetSendTypeId) {
		this.paysheetSendTypeId = paysheetSendTypeId;
	}

	public Byte getPaysheetSendType() {
		return paysheetSendType;
	}

	public void setPaysheetSendType(Byte paysheetSendType) {
		this.paysheetSendType = paysheetSendType;
	}

	public Integer getPaysheetEmailId() {
		return paysheetEmailId;
	}

	public void setPaysheetEmailId(Integer paysheetEmailId) {
		this.paysheetEmailId = paysheetEmailId;
	}

	public String getPaysheetEmail() {
		return paysheetEmail;
	}

	public void setPaysheetEmail(String paysheetEmail) {
		this.paysheetEmail = paysheetEmail;
	}

	public Integer getEnterpriseAgreementId() {
		return enterpriseAgreementId;
	}

	public void setEnterpriseAgreementId(Integer enterpriseAgreementId) {
		this.enterpriseAgreementId = enterpriseAgreementId;
	}

	public String getEnterpriseAgreement() {
		return enterpriseAgreement;
	}

	public void setEnterpriseAgreement(String enterpriseAgreement) {
		this.enterpriseAgreement = enterpriseAgreement;
	}

	public Integer getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Integer calendarId) {
		this.calendarId = calendarId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

}
