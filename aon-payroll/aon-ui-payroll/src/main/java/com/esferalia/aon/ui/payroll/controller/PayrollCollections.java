package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.employee.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.core.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.CausaSuspension;

public class PayrollCollections implements Serializable {

	private static final long serialVersionUID = -3593518156071895968L;
	
	private List<SelectItem> workTimes;
	private List<SelectItem> fileStatus;
	private List<SelectItem> causaSuspension;

	public List<SelectItem> getWorkTimes() {
		if (workTimes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			workTimes = new LinkedList<SelectItem>();
			ContractWorkingDay[] times = ContractWorkingDay.values();
			for (ContractWorkingDay wt : times) {
				String name = wt.getName(locale);
				SelectItem item = new SelectItem(wt, name);
				workTimes.add(item);
			}
		}
		return workTimes;
	}
	
	public List<SelectItem> getFileStatus() {
		if (fileStatus == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			fileStatus = new LinkedList<SelectItem>();
			FileStatus[] estados = FileStatus.values();
			for (FileStatus c : estados) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				fileStatus.add(item);
			}
		}
		return fileStatus;
	}
	
	public List<SelectItem> getCausaSuspension() {
		if (causaSuspension == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			causaSuspension = new LinkedList<SelectItem>();
			CausaSuspension[] causas = CausaSuspension.values();
			for (CausaSuspension c : causas) {
				String name = c.getFullName(locale);
				SelectItem item = new SelectItem(c, name);
				causaSuspension.add(item);
			}
		}
		return causaSuspension;
	}
	
	
}
