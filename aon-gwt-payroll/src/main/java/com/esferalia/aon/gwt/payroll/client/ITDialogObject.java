package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;

public class ITDialogObject {
	
	private ITEmployee itEmployee;
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
		
	// ------------------------------------------------- CLASS METHODS -------------------------------------------------	
	
	public ITDialogObject(ITEmployee itEmployee) {
		this.itEmployee = itEmployee;
	}

	public List<IT> getITList() {
		return itEmployee.getIts();
	}

	public void addITPart(IT it, ITPart newITPart) {
		if(null == it.getITParts())
			it.setITParts(new ArrayList<ITPart>());
		
		it.addITPart(newITPart);
	}

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

	public IT getIT(Integer itId) {
		for(IT it : itEmployee.getIts())
			if(it.getId() == itId || it.getId().equals(itId))
				return it;
			
		return null;
	}
	
	// Return true if alta else baja
	public boolean getEmployeeStatus() {
		return this.itEmployee.getStatus() == (byte)0 ? true : false;
	}

	public void addIT(IT it) {
		this.itEmployee.addIT(it);
	}

	public IT checkIfIsOpenIt() {
		for(IT it : this.itEmployee.getIts()) {
			if(null == it.getEndDate())
				return it;
		}
		
		return null;
	}
	
	
}
