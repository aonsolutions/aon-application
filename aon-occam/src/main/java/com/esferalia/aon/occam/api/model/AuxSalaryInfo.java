package com.esferalia.aon.occam.api.model;

import java.util.ArrayList;
import java.util.List;

public class AuxSalaryInfo {

	private Integer id;
	private Integer domain;
	private Integer contract;
	private String startDate;
	private String endDate;
	private byte type;
	private String enterpriseName;
	private Integer enterpriseId;
	private String workplaceName;
	private Integer workplaceId;
	private String employeeName;
	private Double totalPayment;
	private Double totalDeduction;
	private Double totalLiquid;
	private String issueDate;
	private List<String> idList;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDomain() {
		return domain;
	}
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	public Integer getContract() {
		return contract;
	}
	public void setContract(Integer contract) {
		this.contract = contract;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public Integer getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getWorkplaceName() {
		return workplaceName;
	}
	public void setWorkplaceName(String workplaceName) {
		this.workplaceName = workplaceName;
	}
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public Double getTotalPayment() {
		return totalPayment;
	}
	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}
	public Double getTotalDeduction() {
		return totalDeduction;
	}
	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}
	public Double getTotalLiquid() {
		return totalLiquid;
	}
	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public List<String> getIdList() {
		return idList;
	}
	public void setIdList(List<String> idList) {
		this.idList = idList;
	}
}
