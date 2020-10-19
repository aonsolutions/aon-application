package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.view.client.ProvidesKey;

public class EmployeeContractInfo implements Serializable{
	
	private EmployeeInfo employeeInfo;
	private ContractInfo contractInfo;
	
	private ContractSpecificData contractSpecificData;
	private Map<String, String> contractOtherData;
	private List<ContractClause> contractClauses;
	private List<ContractAttach> contractAttachments;
	private Map<String, String> scopeMap;
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<EmployeeContractInfo> KEY_PROVIDER = new ProvidesKey<EmployeeContractInfo>() {
      @Override
      public Object getKey(EmployeeContractInfo item) {
        return item == null ? null : item.getContractInfo().getContractId();
      }
    };
	
	public EmployeeContractInfo(){
		super();
		this.contractSpecificData = new ContractSpecificData();
		this.contractOtherData = new HashMap<String, String>();
		this.contractClauses = new ArrayList<ContractClause>();
		this.contractAttachments = new ArrayList<ContractAttach>();
		this.scopeMap = new HashMap<String, String>();
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

	public List<ContractAttach> getContractAttachments() {
		return contractAttachments;
	}

	public void setContractAttachments(List<ContractAttach> contractAttachments) {
		this.contractAttachments = contractAttachments;
	}
	
	public void addContractAttach(ContractAttach contractAttach) {
		this.contractAttachments.add(contractAttach);
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

}