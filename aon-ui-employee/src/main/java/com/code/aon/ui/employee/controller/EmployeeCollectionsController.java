package com.code.aon.ui.employee.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.employee.enumeration.ContractDuration;
import com.code.aon.employee.enumeration.ContractTrackingType;
import com.code.aon.employee.enumeration.ContractWorkingDay;

public class EmployeeCollectionsController {

	private List<SelectItem> contractDurations;
	
	private List<SelectItem> contractWorkingDays;
	
	private List<SelectItem> contractTrackingTypes;
	
	public List<SelectItem> getContractDurations() {
		if (contractDurations == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractDurations = new LinkedList<SelectItem>();
			for( ContractDuration contractDuration : ContractDuration.values() ) {
				String name = contractDuration.getName(locale);
				SelectItem item = new SelectItem(contractDuration, name);
				contractDurations.add(item);			
			}
		}
		return contractDurations;
	}

	public List<SelectItem> getContractWorkingDays() {
		if (contractWorkingDays == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractWorkingDays = new LinkedList<SelectItem>();
			for( ContractWorkingDay contractWorkingDay : ContractWorkingDay.values() ) {
				String name = contractWorkingDay.getName(locale);
				SelectItem item = new SelectItem(contractWorkingDay, name);
				contractWorkingDays.add(item);			
			}
		}
		return contractWorkingDays;
	}
	
	public List<SelectItem> getContractTrackingTypes() {
		if (contractTrackingTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractTrackingTypes = new LinkedList<SelectItem>();
			for( ContractTrackingType contractTrackingType : ContractTrackingType.values() ) {
				String name = contractTrackingType.getName(locale);
				SelectItem item = new SelectItem(contractTrackingType, name);
				contractTrackingTypes.add(item);			
			}
		}
		return contractTrackingTypes;
	}
	
}