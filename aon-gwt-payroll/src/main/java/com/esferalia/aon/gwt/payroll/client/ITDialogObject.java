package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;

public class ITDialogObject {
	
	// --------------------------------------------------- Variables
	
	private ITEmployee itEmployee;
	
	// --------------------------------------------------- Constructor
	
	public ITDialogObject(ITEmployee itEmployee) {
		this.itEmployee = itEmployee;
	}
	
	// --------------------------------------------------- IT.Methods

	public List<IT> getITList() {
		return itEmployee.getIts();
	}
	
	public IT getIT(Integer itId) {
		for(IT it : itEmployee.getIts())
			if(it.getId() == itId || it.getId().equals(itId))
				return it;
			
		return null;
	}
	
	public void addIT(IT it) {
		this.itEmployee.addIT(it);
	}

	public void addITPart(IT it, ITPart newITPart) {
		if(null == it.getITParts())
			it.setITParts(new ArrayList<ITPart>());
		
		it.addITPart(newITPart);
	}
	
	public IT checkIfIsOpenIt() {
		for(IT it : this.itEmployee.getIts()) {
			if(null == it.getEndDate())
				return it;
		}
		
		return null;
	}
	
	public Date getRaggedDate(String raggedValue) {
		Integer raggedId = Integer.parseInt(raggedValue);
		
		for(IT it : this.itEmployee.getIts()) {
			if(raggedId == it.getId() || raggedId.equals(it.getId()))
				return it.getStartDate();
		}
		
		return null;
	}

	// --------------------------------------------------- EmployeeStatus
	
	public boolean getEmployeeStatus() {
		// True : ALTA , false : BAJA
		return this.itEmployee.getStatus() == (byte)0 ? true : false;
	}

	public ITEmployee getITEmployee(){
		return this.itEmployee;
	}

	public String getEmployeeName() {
		return getEmployeeinfo().getFullName();
	}
	
	public EmployeeInfo getEmployeeinfo() {
		return this.itEmployee.getEmployeeInfo();
	}
	public ContractInfo getContractInfo() {
		return this.itEmployee.getContractInfo();
	}
	
	public Optional<ITPart> getITBaja(IT it) {
		return it.getITParts().stream().filter(part-> part.getId()!=null && part.getType() == (byte)0).findFirst();
	}
	public Optional<ITPart> getITAlta(IT it) {
		return it.getITParts().stream().filter(part-> part.getId()!=null && part.getType() == (byte)2).findFirst();
	}
}
