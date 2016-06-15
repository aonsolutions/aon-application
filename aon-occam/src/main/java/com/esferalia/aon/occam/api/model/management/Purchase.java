package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.PurchaseStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseType;

public class Purchase implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5561366686830355765L;
	
	private Integer id;
	private int domain;
	private Integer project;
	private int supplier;
	private String series;
	private int number;
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
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public Integer getProject() {
		return project;
	}
	public void setProject(Integer project) {
		this.project = project;
	}
	public int getSupplier() {
		return supplier;
	}
	public void setSupplier(int supplier) {
		this.supplier = supplier;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getPurchaseReference() {
		return purchaseReference;
	}
	public void setPurchaseReference(String purchaseReference) {
		this.purchaseReference = purchaseReference;
	}
	public Integer getAddress() {
		return address;
	}
	public void setAddress(Integer address) {
		this.address = address;
	}
	public String getDiscountExpr() {
		return discountExpr;
	}
	public void setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}
	public PurchaseType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(PurchaseType documentType) {
		this.documentType = documentType;
	}
	public int getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}
	public PurchaseStatus getStatus() {
		return status;
	}
	public void setStatus(PurchaseStatus status) {
		this.status = status;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getWorkplace() {
		return workplace;
	}
	public void setWorkplace(int workplace) {
		this.workplace = workplace;
	}
	public Integer getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Integer warehouse) {
		this.warehouse = warehouse;
	}
	public Integer getScope() {
		return scope;
	}
	public void setScope(Integer scope) {
		this.scope = scope;
	}
	public int getNumberOfPymnts() {
		return numberOfPymnts;
	}
	public void setNumberOfPymnts(int numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
	}
	public int getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	public void setDaysToFirstPymnt(int daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
	}
	public int getDaysBetweenPymnts() {
		return daysBetweenPymnts;
	}
	public void setDaysBetweenPymnts(int daysBetweenPymnts) {
		this.daysBetweenPymnts = daysBetweenPymnts;
	}
	public String getPymntDays() {
		return pymntDays;
	}
	public void setPymntDays(String pymntDays) {
		this.pymntDays = pymntDays;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getBankAlias() {
		return bankAlias;
	}
	public void setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
	}
	public String getBic() {
		return bic;
	}
	public void setBic(String bic) {
		this.bic = bic;
	}
	public boolean isEmailCommunication() {
		return emailCommunication;
	}
	public void setEmailCommunication(boolean emailCommunication) {
		this.emailCommunication = emailCommunication;
	}
	public Integer getCarrier() {
		return carrier;
	}
	public void setCarrier(Integer carrier) {
		this.carrier = carrier;
	}
	public String getShippingAlternativeAddress() {
		return shippingAlternativeAddress;
	}
	public void setShippingAlternativeAddress(String shippingAlternativeAddress) {
		this.shippingAlternativeAddress = shippingAlternativeAddress;
	}
	public String getShippingAlternativeAddress2() {
		return shippingAlternativeAddress2;
	}
	public void setShippingAlternativeAddress2(String shippingAlternativeAddress2) {
		this.shippingAlternativeAddress2 = shippingAlternativeAddress2;
	}
	public String getShippingAlternativeZip() {
		return shippingAlternativeZip;
	}
	public void setShippingAlternativeZip(String shippingAlternativeZip) {
		this.shippingAlternativeZip = shippingAlternativeZip;
	}
	public String getShippingAlternativeCity() {
		return shippingAlternativeCity;
	}
	public void setShippingAlternativeCity(String shippingAlternativeCity) {
		this.shippingAlternativeCity = shippingAlternativeCity;
	}
	public String getShippingAlternativePhone() {
		return shippingAlternativePhone;
	}
	public void setShippingAlternativePhone(String shippingAlternativePhone) {
		this.shippingAlternativePhone = shippingAlternativePhone;
	}
	public String getShippingAlternativeRecipient() {
		return shippingAlternativeRecipient;
	}
	public void setShippingAlternativeRecipient(String shippingAlternativeRecipient) {
		this.shippingAlternativeRecipient = shippingAlternativeRecipient;
	}
	public String getShippingContact() {
		return shippingContact;
	}
	public void setShippingContact(String shippingContact) {
		this.shippingContact = shippingContact;
	}
	public Integer getShippingPeriod() {
		return shippingPeriod;
	}
	public void setShippingPeriod(Integer shippingPeriod) {
		this.shippingPeriod = shippingPeriod;
	}
	
}

