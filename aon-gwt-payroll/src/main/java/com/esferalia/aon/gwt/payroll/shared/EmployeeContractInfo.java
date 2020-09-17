package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

import com.google.gwt.view.client.ProvidesKey;

public class EmployeeContractInfo implements Serializable{
	
	private EmployeeInfo employeeInfo;
	private ContractInfo contractInfo;
	
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

}