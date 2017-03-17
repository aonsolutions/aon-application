package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Audit;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.type.IncomeStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonJSONUtils;

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
	
	public String toJSON() {
		StringBuilder json = new StringBuilder();
		json.append(AonJSONUtils.start());
		json.append(AonJSONUtils.intToJSON("id", getId(), false));
		json.append(AonJSONUtils.intToJSON("domain", getDomain(), false));
		// TODO json.append(AonJSONUtils.intToJSON("project", getProject(), false));
		json.append(AonJSONUtils.start("registry")); //TODO supplier o registry ¿?
			json.append(AonJSONUtils.intToJSON("id", getSupplier(), true));
			//json.append(AonJSONUtils.strToJSON("name", getSupplierName(), true));
		json.append(AonJSONUtils.end() + ",");
		json.append(AonJSONUtils.strToJSON("reference_code", getReferenceCode(), false));
		json.append(AonJSONUtils.intToJSON("address", getAddress(), false));
		//	TODO SE USA EN PACKING LIST - HAY K CAMBIARLO!
		json.append(AonJSONUtils.dateToJSON("issue_date", getIssueDate(), false));
		//
		json.append(AonJSONUtils.intToJSON("pay_method",  getPayMethod(), false));
		
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
		json.append(AonJSONUtils.intToJSON("scope", getScope(), false));
		json.append(AonJSONUtils.intToJSON("number_of_pymnts", getNumberOfPymnts(), false));
		json.append(AonJSONUtils.intToJSON("days_to_first_pymnt", getDaysToFirstPymnt(), false));
		json.append(AonJSONUtils.intToJSON("days_between_pymnts", getDaysBetweenPymnt(), false));
		json.append(AonJSONUtils.strToJSON("pymnt_days", getPymntDays(), false));
		json.append(AonJSONUtils.strToJSON("bankAccount", getBankAccount(), false));
		json.append(AonJSONUtils.strToJSON("bankAlias", getBankAlias(), false));
		json.append(AonJSONUtils.strToJSON("bic", getBic(), false));
		json.append(AonJSONUtils.intToJSON("carrier_packing", getCarrierPacking(), false));
		
		json.append(AonJSONUtils.strToJSON("creation_user", getCreationUser(), false));
		json.append(AonJSONUtils.dateToJSON("creation_date", getCreationDate(), false));
		json.append(AonJSONUtils.strToJSON("modification_user", getModificationUser(), false));
		json.append(AonJSONUtils.dateToJSON("modification_date", getModificationDate(), false));
		
		// TODO se utilizan en pantalla de packing list!!! 
		json.append(AonJSONUtils.strToJSON("series_number", "",false));
		json.append(AonJSONUtils.strToJSON("order_type", "purchase", false));
		json.append(AonJSONUtils.strToJSON("reference",getReferenceCode() != null ? getReferenceCode() : " ", true));
		
		json.append(AonJSONUtils.end());
		
		return json.toString();
	}

}
