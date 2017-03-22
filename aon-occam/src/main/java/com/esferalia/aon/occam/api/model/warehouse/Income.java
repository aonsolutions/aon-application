package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Audit;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.type.IncomeStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class Income extends Audit implements Serializable {
	
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
	private Integer carrierPacking;

	
	public Integer getId() {
		return id;
	}
	public Income setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Income setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Project getProject() {
		return project;
	}
	public Income setProject(Project project) {
		this.project = project;
		return this;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public Income setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	public Integer getAddress() {
		return address;
	}
	public Income setAddress(Integer address) {
		this.address = address;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public Income setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public Income setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	public Integer getSecurityLevel() {
		return securityLevel;
	}
	public Income setSecurityLevel(Integer securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public Boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL.value().equals(securityLevel);
	}
	
	public String getComments() {
		return comments;
	}
	public Income setComments(String comments) {
		this.comments = comments;
		return this;
	}
	public String getRemarks() {
		return remarks;
	}
	public Income setRemarks(String remarks) {
		this.remarks = remarks;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public Income setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public Integer getNumberOfPymnts() {
		return numberOfPymnts;
	}
	public Income setNumberOfPymnts(Integer numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
		return this;
	}
	public Integer getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	public Income setDaysToFirstPymnt(Integer daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
		return this;
	}
	public Integer getDaysBetweenPymnt() {
		return daysBetweenPymnt;
	}
	public Income setDaysBetweenPymnt(Integer daysBetweenPymnt) {
		this.daysBetweenPymnt = daysBetweenPymnt;
		return this;
	}
	public String getPymntDays() {
		return pymntDays;
	}
	public Income setPymntDays(String pymntDays) {
		this.pymntDays = pymntDays;
		return this;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public Income setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	public String getBankAlias() {
		return bankAlias;
	}
	public Income setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
		return this;
	}
	public String getBic() {
		return bic;
	}
	public Income setBic(String bic) {
		this.bic = bic;
		return this;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Integer getSupplier() {
		return supplier;
	}
	public Income setSupplier(Integer supplier) {
		this.supplier = supplier;
		return this;
	}
	public IncomeStatus getStatus() {
		return status;
	}
	public Income setStatus(IncomeStatus status) {
		this.status = status;
		return this;
	}
	public Integer getWorkplace() {
		return workplace;
	}
	public Income setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	public Integer getCarrierPacking() {
		return carrierPacking;
	}
	public Income setCarrierPacking(Integer carrierPacking) {
		this.carrierPacking = carrierPacking;
		return this;
	}

}
