package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.DateUtils;
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
	
	// --------------------------------------------------- ConfirmationParts.Methods

	public void deleteConfirmationPart(Integer itId, ITPart itPart) {
		int deleteIdx = -1;
		for(IT it : itEmployee.getIts()) {
			if(it.getId() == itId || it.getId().equals(itId)) {
				for(int i=0; i<it.getITParts().size(); i++) {
					if(DateUtils.equals(itPart.getDate(), it.getITParts().get(i).getDate())) {
						deleteIdx = i;
						break;
					}		
				}
				it.getITParts().remove(deleteIdx);
				break;
			}
		}
	}

	// --------------------------------------------------- EmployeeStatus
	
	public boolean getEmployeeStatus() {
		// True : ALTA , false : BAJA
		return this.itEmployee.getStatus() == (byte)0 ? true : false;
	}

	public String getEmployeeName() {
		return this.itEmployee.getEmployeeInfo().getFullName();
	}

}
