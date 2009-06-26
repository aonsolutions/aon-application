package com.code.aon.ui.registry.controller;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.ui.form.LinesController;

public class RegistryDirStaffController extends LinesController {
	
	@SuppressWarnings("unchecked")
	public double getTotalPercentShare() throws ManagerBeanException{
		double total = 0.0;
		Iterator iter = ((List)getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			RegistryDirStaff rDirStaff = (RegistryDirStaff)iter.next();
			total += rDirStaff.getPercentShare();
		}
		return total;
	}
	
	@SuppressWarnings("unchecked")
	public int getTotalShareNumber() throws ManagerBeanException{
		int total = 0;
		Iterator iter = ((List)getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			RegistryDirStaff rDirStaff = (RegistryDirStaff)iter.next();
			total += rDirStaff.getShareNumber();
		}
		return total;
	}
	
	@SuppressWarnings("unchecked")
	public double getTotalNominalValue() throws ManagerBeanException{
		double total = 0.0;
		Iterator iter = ((List)getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			RegistryDirStaff rDirStaff = (RegistryDirStaff)iter.next();
			total += rDirStaff.getNominalValue();
		}
		return total;
	}
}
