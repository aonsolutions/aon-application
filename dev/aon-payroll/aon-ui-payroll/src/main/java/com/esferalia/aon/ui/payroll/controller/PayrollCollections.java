package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.employee.enumeration.ContractModel;
import com.code.aon.employee.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.CausaSuspension;
import com.esferalia.aon.payroll.core.enumeration.FileStatus;
//import com.esferalia.aon.payroll.enumeration.ContractModel;

public class PayrollCollections implements Serializable {

	private static final long serialVersionUID = -3593518156071895968L;
	
	private List<SelectItem> contractModels;
	private List<SelectItem> workTimes;
	private List<SelectItem> fileStatus;
	private List<SelectItem> causaSuspension;

	public List<SelectItem> getContractModels() {
		if (contractModels == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			contractModels = new LinkedList<SelectItem>();
			ContractModel[] models = ContractModel.values();
			for (ContractModel cm : models) {
				String name = cm.getName(locale)+" - ";
				if(cm.getDescription(locale).length()>70){
					name += cm.getDescription(locale).substring(0, 70)+"...";
				} else {
					name += cm.getDescription(locale);
				}
				SelectItem item = new SelectItem(cm, name);
				if(cm.getName(locale).equals("PE170") || cm.getName(locale).equals("PE177"))
				contractModels.add(item);
			}
		}
		return contractModels;
	}
	
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
