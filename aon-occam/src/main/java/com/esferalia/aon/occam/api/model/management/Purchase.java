package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.PurchaseStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonJSONUtils;

public class Purchase implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5561366686830355765L;
	
	private Integer id;
	private int domain;
	private Integer project;
	private int supplier;
	private String supplierName;
	private String series;
	private Integer number;
	private String purchaseReference;
	private Integer address;
	private String discountExpr;
	private Date issueDate;
	private Integer payMethod;
	private PurchaseType documentType;
	private int securityLevel;
	private PurchaseStatus status;
	private String comments;
	private String remarks;
	private int workplace;
	private Integer warehouse;
	private Integer scope;
	private int numberOfPymnts;
	private int daysToFirstPymnt;
	private int daysBetweenPymnts;
	private String pymntDays;
	private String bankAccount;
	private String bankAlias;
	private String bic;
	private boolean emailCommunication;
	private Integer carrier;
	private String shippingAlternativeAddress;
	private String shippingAlternativeAddress2;
	private String shippingAlternativeZip;
	private String shippingAlternativeCity;
	private String shippingAlternativePhone;
	private String shippingAlternativeRecipient;
	private String shippingContact;
	private Integer shippingPeriod;
	private Integer carrierPacking;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Integer getId() {
		return id;
	}
	public Purchase setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Purchase setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getProject() {
		return project;
	}
	public Purchase setProject(Integer project) {
		this.project = project;
		return this;
	}
	public int getSupplier() {
		return supplier;
	}
	public Purchase setSupplier(int supplier) {
		this.supplier = supplier;
		return this;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public Purchase setSupplierName(String supplierName) {
		this.supplierName = supplierName;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public Purchase setSeries(String series) {
		this.series = series;
		return this;
	}
	public Integer getNumber() {
		return number;
	}
	public Purchase setNumber(Integer number) {
		this.number = number;
		return this;
	}
	public String getPurchaseReference() {
		return purchaseReference;
	}
	public Purchase setPurchaseReference(String purchaseReference) {
		this.purchaseReference = purchaseReference;
		return this;
	}
	public Integer getAddress() {
		return address;
	}
	public Purchase setAddress(Integer address) {
		this.address = address;
		return this;
	}
	public String getDiscountExpr() {
		return discountExpr;
	}
	public Purchase setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public Purchase setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public Purchase setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	public PurchaseType getDocumentType() {
		return documentType;
	}
	public Purchase setDocumentType(PurchaseType documentType) {
		this.documentType = documentType;
		return this;
	}
	public int getSecurityLevel() {
		return securityLevel;
	}
	public Purchase setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}

	public Boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL.value().equals(securityLevel);
	}
	
	public PurchaseStatus getStatus() {
		return status;
	}
	public Purchase setStatus(PurchaseStatus status) {
		this.status = status;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public Purchase setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public String getRemarks() {
		return remarks;
	}
	public Purchase setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
	public int getWorkplace() {
		return workplace;
	}
	public Purchase setWorkplace(int workplace) {
		this.workplace = workplace;
		return this;
	}
	public Integer getWarehouse() {
		return warehouse;
	}
	public Purchase setWarehouse(Integer warehouse) {
		this.warehouse = warehouse;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public Purchase setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public int getNumberOfPymnts() {
		return numberOfPymnts;
	}
	public Purchase setNumberOfPymnts(int numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
		return this;
	}
	public int getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	public Purchase setDaysToFirstPymnt(int daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
		return this;
	}
	public int getDaysBetweenPymnts() {
		return daysBetweenPymnts;
	}
	public Purchase setDaysBetweenPymnts(int daysBetweenPymnts) {
		this.daysBetweenPymnts = daysBetweenPymnts;
		return this;
	}
	public String getPymntDays() {
		return pymntDays;
	}
	public Purchase setPymntDays(String pymntDays) {
		this.pymntDays = pymntDays;
		return this;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public Purchase setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	public String getBankAlias() {
		return bankAlias;
	}
	public Purchase setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
		return this;
	}
	public String getBic() {
		return bic;
	}
	public Purchase setBic(String bic) {
		this.bic = bic;
		return this;
	}
	public boolean isEmailCommunication() {
		return emailCommunication;
	}
	public Purchase setEmailCommunication(boolean emailCommunication) {
		this.emailCommunication = emailCommunication;
		return this;
	}
	public Integer getCarrier() {
		return carrier;
	}
	public Purchase setCarrier(Integer carrier) {
		this.carrier = carrier;
		return this;
	}
	public String getShippingAlternativeAddress() {
		return shippingAlternativeAddress;
	}
	public Purchase setShippingAlternativeAddress(String shippingAlternativeAddress) {
		this.shippingAlternativeAddress = shippingAlternativeAddress;
		return this;
	}
	public String getShippingAlternativeAddress2() {
		return shippingAlternativeAddress2;
	}
	public Purchase setShippingAlternativeAddress2(String shippingAlternativeAddress2) {
		this.shippingAlternativeAddress2 = shippingAlternativeAddress2;
		return this;
	}
	public String getShippingAlternativeZip() {
		return shippingAlternativeZip;
	}
	public Purchase setShippingAlternativeZip(String shippingAlternativeZip) {
		this.shippingAlternativeZip = shippingAlternativeZip;
		return this;
	}
	public String getShippingAlternativeCity() {
		return shippingAlternativeCity;
	}
	public Purchase setShippingAlternativeCity(String shippingAlternativeCity) {
		this.shippingAlternativeCity = shippingAlternativeCity;
		return this;
	}
	public String getShippingAlternativePhone() {
		return shippingAlternativePhone;
	}
	public Purchase setShippingAlternativePhone(String shippingAlternativePhone) {
		this.shippingAlternativePhone = shippingAlternativePhone;
		return this;
	}
	public String getShippingAlternativeRecipient() {
		return shippingAlternativeRecipient;
	}
	public Purchase setShippingAlternativeRecipient(String shippingAlternativeRecipient) {
		this.shippingAlternativeRecipient = shippingAlternativeRecipient;
		return this;
	}
	public String getShippingContact() {
		return shippingContact;
	}
	public Purchase setShippingContact(String shippingContact) {
		this.shippingContact = shippingContact;
		return this;
	}
	public Integer getShippingPeriod() {
		return shippingPeriod;
	}
	public Purchase setShippingPeriod(Integer shippingPeriod) {
		this.shippingPeriod = shippingPeriod;
		return this;
	}
	public Integer getCarrierPacking() {
		return carrierPacking;
	}
	public Purchase setCarrierPacking(Integer carrierPacking) {
		this.carrierPacking = carrierPacking;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public Purchase setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public Purchase setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public Purchase setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public Purchase setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public String toJSON() {
		StringBuilder json = new StringBuilder();
		json.append(AonJSONUtils.start());
		json.append(AonJSONUtils.intToJSON("id", getId(), false));
		json.append(AonJSONUtils.intToJSON("domain", getDomain(), false));
		json.append(AonJSONUtils.intToJSON("project", getProject(), false));
		json.append(AonJSONUtils.start("registry")); //TODO supplier o registry ¿?
			json.append(AonJSONUtils.intToJSON("id", getSupplier(), false));
			json.append(AonJSONUtils.strToJSON("name", getSupplierName(), true));
		json.append(AonJSONUtils.end() + ",");
		json.append(AonJSONUtils.strToJSON("series", getSeries(), false));
		json.append(AonJSONUtils.intToJSON("number", getNumber(), false));	
		json.append(AonJSONUtils.strToJSON("purchase_reference", getPurchaseReference(), false));
		json.append(AonJSONUtils.intToJSON("address", getAddress(), false));
		json.append(AonJSONUtils.strToJSON("discount_expr", getDiscountExpr(), false));
		//	TODO SE USA EN PACKING LIST - HAY K CAMBIARLO!
		json.append(AonJSONUtils.dateToJSON("issue_date", getIssueDate(), false));
		//
		json.append(AonJSONUtils.intToJSON("pay_method",  getPayMethod(), false));
		if(getDocumentType() != null){
			json.append(AonJSONUtils.start("document_type"));
				json.append(AonJSONUtils.intToJSON("id", getDocumentType().ordinal(), false));
				json.append(AonJSONUtils.strToJSON("name", getDocumentType().getName(), true));
			json.append(AonJSONUtils.end() + ",");
		}
		json.append(AonJSONUtils.boolToJSON("confidential", isConfidential(), false));
		if(getStatus() != null){
			json.append(AonJSONUtils.start("status"));
				json.append(AonJSONUtils.intToJSON("id", getStatus().ordinal(), false));
				json.append(AonJSONUtils.strToJSON("name", getStatus().getName(), true));
			json.append(AonJSONUtils.end() + ",");
		}
		json.append(AonJSONUtils.strToJSON("comments", getComments(), false));
		json.append(AonJSONUtils.strToJSON("remarks", getRemarks(), false));
		json.append(AonJSONUtils.intToJSON("workplace", getWorkplace(), false));
		json.append(AonJSONUtils.intToJSON("warehouse", getWarehouse(), false));
		json.append(AonJSONUtils.intToJSON("scope", getScope(), false));
		json.append(AonJSONUtils.intToJSON("number_of_pymnts", getNumberOfPymnts(), false));
		json.append(AonJSONUtils.intToJSON("days_to_first_pymnt", getDaysToFirstPymnt(), false));
		json.append(AonJSONUtils.intToJSON("days_between_pymnts", getDaysBetweenPymnts(), false));
		json.append(AonJSONUtils.strToJSON("pymnt_days", getPymntDays(), false));
		json.append(AonJSONUtils.strToJSON("bankAccount", getBankAccount(), false));
		json.append(AonJSONUtils.strToJSON("bankAlias", getBankAlias(), false));
		json.append(AonJSONUtils.strToJSON("bic", getBic(), false));
		json.append(AonJSONUtils.boolToJSON("email_communication", isEmailCommunication(), false));
		json.append(AonJSONUtils.intToJSON("carrier", getCarrier(), false));
		json.append(AonJSONUtils.intToJSON("carrier_packing", getCarrierPacking(), false));
		json.append(AonJSONUtils.strToJSON("shipping_alternative_address", getShippingAlternativeAddress(), false));
		json.append(AonJSONUtils.strToJSON("shipping_alternative_address2", getShippingAlternativeAddress2(), false));
		json.append(AonJSONUtils.strToJSON("shipping_alternative_zip", getShippingAlternativeZip(), false));
		json.append(AonJSONUtils.strToJSON("shipping_alternative_city", getShippingAlternativeCity(), false));
		json.append(AonJSONUtils.strToJSON("shipping_alternative_phone", getShippingAlternativePhone(), false));
		json.append(AonJSONUtils.strToJSON("shipping_alternative_recipient", getShippingAlternativeRecipient(), false));
		json.append(AonJSONUtils.strToJSON("shipping_contact", getShippingContact(), false));
		json.append(AonJSONUtils.intToJSON("shipping_period", getShippingPeriod(), false));
		
		json.append(AonJSONUtils.strToJSON("creation_user", getCreationUser(), false));
		json.append(AonJSONUtils.dateToJSON("creation_date", getCreationDate(), false));
		json.append(AonJSONUtils.strToJSON("modification_user", getModificationUser(), false));
		json.append(AonJSONUtils.dateToJSON("modification_date", getModificationDate(), false));
		
		// TODO se utilizan en pantalla de packing list!!! 
		json.append(AonJSONUtils.strToJSON("series_number", "",false));
		json.append(AonJSONUtils.strToJSON("order_type", "purchase", false));
		json.append(AonJSONUtils.strToJSON("reference",getPurchaseReference() != null ? getPurchaseReference() : " ", true));
		
		json.append(AonJSONUtils.end());
		
		return json.toString();
	}
}

