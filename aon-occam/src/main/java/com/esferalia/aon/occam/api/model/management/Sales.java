package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.type.SalesStatus;

public class Sales implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3076493407383383282L;
	private Integer id;
	private int domain;
	private Integer project;
	private Customer customer;
	private String series;
	private int number;
	private String purchaseReference;
	private Integer shippingAddress;
	private Integer seller;
	private String discountExpr;
	private Date issueDate;
	private Integer payMethod;
	private int documentType;
	private int securityLevel;
	private SalesStatus status;
	private String comments;
	private String remarks;
	private int workplace;
	private int scope;
	private int numberOfPymnts;
	private int daysToFirstPymnt;
	private int daysBetweenPymnts;
	private String pymntDays;
	private String bankAccount;
	private String bankAlias;
	private String bic;
	private boolean purchaseGenerated;
	private Date deliveryDate;
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
	public Sales setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Sales setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Integer getProject() {
		return project;
	}
	public Sales setProject(Integer project) {
		this.project = project;
		return this;
	}
	public Customer getCustomer() {
		return customer;
	}
	public Sales setCustomer(Customer customer) {
		this.customer = customer;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public Sales setSeries(String series) {
		this.series = series;
		return this;
	}
	public int getNumber() {
		return number;
	}
	public Sales setNumber(int number) {
		this.number = number;
		return this;
	}
	public String getPurchaseReference() {
		return purchaseReference;
	}
	public Sales setPurchaseReference(String purchaseReference) {
		this.purchaseReference = purchaseReference;
		return this;
	}
	public Integer getShippingAddress() {
		return shippingAddress;
	}
	public Sales setShippingAddress(Integer shippingAddress) {
		this.shippingAddress = shippingAddress;
		return this;
	}
	public Integer getSeller() {
		return seller;
	}
	public Sales setSeller(Integer seller) {
		this.seller = seller;
		return this;
	}
	public String getDiscountExpr() {
		return discountExpr;
	}
	public Sales setDiscountExpr(String discountExpr) {
		this.discountExpr = discountExpr;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public Sales setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public Sales setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	public int getDocumentType() {
		return documentType;
	}
	public Sales setDocumentType(int documentType) {
		this.documentType = documentType;
		return this;
	}
	public int getSecurityLevel() {
		return securityLevel;
	}
	public Sales setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public SalesStatus getStatus() {
		return status;
	}
	public Sales setStatus(SalesStatus status) {
		this.status = status;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public Sales setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public String getRemarks() {
		return remarks;
	}
	public Sales setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
	public int getWorkplace() {
		return workplace;
	}
	public Sales setWorkplace(int workplace) {
		this.workplace = workplace;
		return this;
	}
	public int getScope() {
		return scope;
	}
	public Sales setScope(int scope) {
		this.scope = scope;
		return this;
	}
	public int getNumberOfPymnts() {
		return numberOfPymnts;
	}
	public Sales setNumberOfPymnts(int numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
		return this;
	}
	public int getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	public Sales setDaysToFirstPymnt(int daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
		return this;
	}
	public int getDaysBetweenPymnts() {
		return daysBetweenPymnts;
	}
	public Sales setDaysBetweenPymnts(int daysBetweenPymnts) {
		this.daysBetweenPymnts = daysBetweenPymnts;
		return this;
	}
	public String getPymntDays() {
		return pymntDays;
	}
	public Sales setPymntDays(String pymntDays) {
		this.pymntDays = pymntDays;
		return this;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public Sales setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	public String getBankAlias() {
		return bankAlias;
	}
	public Sales setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
		return this;
	}
	public String getBic() {
		return bic;
	}
	public Sales setBic(String bic) {
		this.bic = bic;
		return this;
	}
	public boolean isPurchaseGenerated() {
		return purchaseGenerated;
	}
	public Sales setPurchaseGenerated(boolean purchaseGenerated) {
		this.purchaseGenerated = purchaseGenerated;
		return this;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public Sales setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
		return this;
	}
	public Integer getCarrier() {
		return carrier;
	}
	public Sales setCarrier(Integer carrier) {
		this.carrier = carrier;
		return this;
	}
	public String getShippingAlternativeAddress() {
		return shippingAlternativeAddress;
	}
	public Sales setShippingAlternativeAddress(String shippingAlternativeAddress) {
		this.shippingAlternativeAddress = shippingAlternativeAddress;
		return this;
	}
	public String getShippingAlternativeAddress2() {
		return shippingAlternativeAddress2;
	}
	public Sales setShippingAlternativeAddress2(String shippingAlternativeAddress2) {
		this.shippingAlternativeAddress2 = shippingAlternativeAddress2;
		return this;
	}
	public String getShippingAlternativeZip() {
		return shippingAlternativeZip;
	}
	public Sales setShippingAlternativeZip(String shippingAlternativeZip) {
		this.shippingAlternativeZip = shippingAlternativeZip;
		return this;
	}
	public String getShippingAlternativeCity() {
		return shippingAlternativeCity;
	}
	public Sales setShippingAlternativeCity(String shippingAlternativeCity) {
		this.shippingAlternativeCity = shippingAlternativeCity;
		return this;
	}
	public String getShippingAlternativePhone() {
		return shippingAlternativePhone;
	}
	public Sales setShippingAlternativePhone(String shippingAlternativePhone) {
		this.shippingAlternativePhone = shippingAlternativePhone;
		return this;
	}
	public String getShippingAlternativeRecipient() {
		return shippingAlternativeRecipient;
	}
	public Sales setShippingAlternativeRecipient(String shippingAlternativeRecipient) {
		this.shippingAlternativeRecipient = shippingAlternativeRecipient;
		return this;
	}
	public String getShippingContact() {
		return shippingContact;
	}
	public Sales setShippingContact(String shippingContact) {
		this.shippingContact = shippingContact;
		return this;
	}
	public Integer getShippingPeriod() {
		return shippingPeriod;
	}
	public Sales setShippingPeriod(Integer shippingPeriod) {
		this.shippingPeriod = shippingPeriod;
		return this;
	}
	
}

