package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.view.client.ProvidesKey;

public class EmployeeContractInfo implements Serializable{
	
	private EmployeeInfo employeeInfo;
	private ContractInfo contractInfo;
	
	private Map<String, String> contractOtherData;
	
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
		this.contractOtherData = new HashMap<String, String>();
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

}