package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;

public class Delivery implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5693712945852955230L;
	private Integer id;
	private int domain;
	private Project project;
	private String series;
	private int number;
	private Integer customer;
	private Integer address;
	private Date issueTime;
	private Integer payMethod;
	private byte securityLevel;
	private DeliveryStatus status; 
	private String comments;
	private String remarks;
	private Integer workplace;
	private Integer scope;
	private short numberOfPymnts;
	private short daysToFirstPymnt;
	private short daysBetweenPymnt;
	private String pymntDays;
	private String bankAccount;
	private String bankAlias;
	private String bic;
	// shippingAlternative
	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
	public Integer getId() {
		return id;
	}
	public Delivery setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Delivery setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Project getProject() {
		return project;
	}
	public Delivery setProject(Project project) {
		this.project = project;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public Delivery setSeries(String series) {
		this.series = series;
		return this;
	}
	public int getNumber() {
		return number;
	}
	public Delivery setNumber(int number) {
		this.number = number;
		return this;
	}
	public Integer getAddress() {
		return address;
	}
	public Delivery setAddress(Integer address) {
		this.address = address;
		return this;
	}
	public Date getIssueTime() {
		return issueTime;
	}
	public Delivery setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
		return this;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public Delivery setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	public byte getSecurityLevel() {
		return securityLevel;
	}
	public Delivery setSecurityLevel(byte securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	public String getComments() {
		return comments;
	}
	public Delivery setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public String getRemarks() {
		return remarks;
	}
	public Delivery setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public Delivery setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public short getNumberOfPymnts() {
		return numberOfPymnts;
	}
	public Delivery setNumberOfPymnts(short numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
		return this;
	}
	public short getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	public Delivery setDaysToFirstPymnt(short daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
		return this;
	}
	public short getDaysBetweenPymnt() {
		return daysBetweenPymnt;
	}
	public Delivery setDaysBetweenPymnt(short daysBetweenPymnt) {
		this.daysBetweenPymnt = daysBetweenPymnt;
		return this;
	}
	public String getPymntDays() {
		return pymntDays;
	}
	public Delivery setPymntDays(String pymntDays) {
		this.pymntDays = pymntDays;
		return this;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public Delivery setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	public String getBankAlias() {
		return bankAlias;
	}
	public Delivery setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
		return this;
	}
	public String getBic() {
		return bic;
	}
	public Delivery setBic(String bic) {
		this.bic = bic;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public Delivery setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public Delivery setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public Delivery setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public Delivery setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Integer getCustomer() {
		return customer;
	}
	public Delivery setCustomer(Integer customer) {
		this.customer = customer;
		return this;
	}
	public DeliveryStatus getStatus() {
		return status;
	}
	public Delivery setStatus(DeliveryStatus status) {
		this.status = status;
		return this;
	}
	public Integer getWorkplace() {
		return workplace;
	}
	public Delivery setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}

	
	

}
