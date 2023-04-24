package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.view.client.ProvidesKey;

public class EmployeeContractInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private EmployeeInfo employeeInfo;
	private ContractInfo contractInfo;
	
	private ContractSpecificData contractSpecificData;
	private Map<String, String> contractOtherData;
	private List<ContractClause> contractClauses;
	private List<Attach> contractAttachments;
	private List<SSBonusData> contractBonus;
	private Map<String, String> scopeMap;
	
	private List<Agreement> agreements;
	private ActivitiesCCC activitiesCCC;
	private List<Workplace> workplaces;
	private Map<String, String> payMethods;
	
	private boolean isModify = false;
	
	public static final ProvidesKey<EmployeeContractInfo> KEY_PROVIDER = item -> item == null ? null : item.getContractInfo().getContractId();
	
	public EmployeeContractInfo(){
		super();
		this.contractSpecificData = new ContractSpecificData();
		this.contractOtherData = new HashMap<>();
		this.contractClauses = new ArrayList<>();
		this.contractAttachments = new ArrayList<>();
		this.contractBonus = new ArrayList<>();
		this.scopeMap = new HashMap<>();
	}
	
	// ------------- GETTERS / SETTERS -------------

	public EmployeeInfo getEmployeeInfo() {
		return employeeInfo;
	}

	public void setEmployeeInfo(EmployeeInfo employeeInfo) {
		this.employeeInfo = employeeInfo;
	}

	public ContractInfo getContractInfo() {
		return contractInfo;
	}

	public void setContractInfo(ContractInfo contractInfo) {
		this.contractInfo = contractInfo;
	}

	public Map<String, String> getContractOtherData() {
		return contractOtherData;
	}

	public void setContractOtherData(Map<String, String> contractOtherData) {
		this.contractOtherData = contractOtherData;
	}
	
	public void addContractOtherData(String name, String value) {
		this.contractOtherData.put(name, value);
	}

	public List<ContractClause> getContractClauses() {
		return contractClauses;
	}

	public void setContractClauses(List<ContractClause> contractClauses) {
		this.contractClauses = contractClauses;
	}
	
	public void addContractClause(ContractClause contractClause) {
		this.contractClauses.add(contractClause);
	}

	public List<Attach> getContractAttachments() {
		return contractAttachments;
	}

	public void setContractAttachments(List<Attach> contractAttachments) {
		this.contractAttachments = contractAttachments;
	}
	
	public void addContractAttach(Attach contractAttach) {
		this.contractAttachments.add(contractAttach);
	}

	public List<SSBonusData> getContractBonus() {
		return contractBonus;
	}

	public void setContractBonus(List<SSBonusData> contractBonus) {
		this.contractBonus = contractBonus;
	}
	
	public void addContractBonus(SSBonusData contractBonus) {
		this.contractBonus.add(contractBonus);
	}
	
	public Map<String, String> getScopeMap() {
		return scopeMap;
	}

	public void setScopeMap(Map<String, String> scopeMap) {
		this.scopeMap = scopeMap;
	}

	public ContractSpecificData getContractSpecificData() {
		return contractSpecificData;
	}

	public void setContractSpecificData(ContractSpecificData contractSpecificData) {
		this.contractSpecificData = contractSpecificData;
	}

	public List<Agreement> getAgreements() {
		return agreements;
	}

	public void setAgreements(List<Agreement> agreements) {
		this.agreements = agreements;
	}

	public ActivitiesCCC getActivitiesCCC() {
		return activitiesCCC;
	}

	public void setActivitiesCCC(ActivitiesCCC activitiesCCC) {
		this.activitiesCCC = activitiesCCC;
	}

	public List<Workplace> getWorkplaces() {
		return workplaces;
	}

	public void setWorkplaces(List<Workplace> workplaces) {
		this.workplaces = workplaces;
	}

	public Map<String, String> getPayMethods() {
		return payMethods;
	}

	public void setPayMethods(Map<String, String> payMethods) {
		this.payMethods = payMethods;
	}

	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}

}