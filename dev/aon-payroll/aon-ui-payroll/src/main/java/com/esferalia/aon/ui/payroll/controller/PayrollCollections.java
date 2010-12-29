package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.employee.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;

public class PayrollCollections implements Serializable {

	private static final long serialVersionUID = -3593518156071895968L;
	
	private List<SelectItem> workTimes;
	private List<SelectItem> fileStatus;
	private List<SelectItem> suspensionCause;

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
	
	public List<SelectItem> getSuspensionCause() {
		if (suspensionCause == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			suspensionCause = new LinkedList<SelectItem>();
			SuspensionCause[] causas = SuspensionCause.values();
			for (SuspensionCause c : causas) {
				String name = c.getFullName(locale);
				SelectItem item = new SelectItem(c, name);
				suspensionCause.add(item);
			}
		}
		return suspensionCause;
	}
	
	
}
