package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.type.IncomeStatus;

public class Income implements Serializable {
	
	private static final long serialVersionUID = 8897444490096530091L;
	
	private Integer id;
	private int domain;
	private Project project;
	private String referenceCode;
	private Integer supplier;
	private Integer address;
	private Date issueDate;
	private Integer payMethod;
	private Integer securityLevel;
	private IncomeStatus status; 
	private String comments;
	private String remarks;
	private Integer workplace;
	private Integer scope;
	private Integer numberOfPymnts;
	private Integer daysToFirstPymnt;
	private Integer daysBetweenPymnt;
	private String pymntDays;
	private String bankAccount;
	private String bankAlias;
	private String bic;

	private Date creationDate;
	private String creationUser;
	private Date modificationDate;
	private String modificationUser;
	
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
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public Integer getAddress() {
		return address;
	}
	public void setAddress(Integer address) {
		this.address = address;
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
	public Integer getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(Integer securityLevel) {
		this.securityLevel = securityLevel;
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
	public Integer getScope() {
		return scope;
	}
	public void setScope(Integer scope) {
		this.scope = scope;
	}
	public Integer getNumberOfPymnts() {
		return numberOfPymnts;
	}
	public void setNumberOfPymnts(Integer numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
	}
	public Integer getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	public void setDaysToFirstPymnt(Integer daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
	}
	public Integer getDaysBetweenPymnt() {
		return daysBetweenPymnt;
	}
	public void setDaysBetweenPymnt(Integer daysBetweenPymnt) {
		this.daysBetweenPymnt = daysBetweenPymnt;
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
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public void setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Integer getSupplier() {
		return supplier;
	}
	public void setSupplier(Integer supplier) {
		this.supplier = supplier;
	}
	public IncomeStatus getStatus() {
		return status;
	}
	public void setStatus(IncomeStatus status) {
		this.status = status;
	}
	public Integer getWorkplace() {
		return workplace;
	}
	public void setWorkplace(Integer workplace) {
		this.workplace = workplace;
	}

	
	

}
