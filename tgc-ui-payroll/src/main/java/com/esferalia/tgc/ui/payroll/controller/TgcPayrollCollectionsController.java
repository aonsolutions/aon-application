package com.esferalia.tgc.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.esferalia.tgc.ui.payroll.enumeration.ReportName;
import com.esferalia.tgc.ui.payroll.enumeration.ReportType;


public class TgcPayrollCollectionsController {

	private List<SelectItem> enterpriseReportNames;
	private List<SelectItem> contractReportNames;
	private List<SelectItem> personReportNames;
	
	
	public List<SelectItem> getEnterpriseReportNames() {
		if (enterpriseReportNames == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			enterpriseReportNames = new LinkedList<SelectItem>();
			for( ReportName n : ReportName.values() ) {
				if(n.getType()==ReportType.ENTERPRISE){
					String name = n.getName(locale);
					SelectItem item = new SelectItem(n, name);
					enterpriseReportNames.add(item);			
				}
			}
		}
		return enterpriseReportNames;
	}
	
	public List<SelectItem> getContractReportNames() {
		if (contractReportNames == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractReportNames = new LinkedList<SelectItem>();
			for( ReportName n : ReportName.values() ) {
				if(n.getType()==ReportType.CONTRACT){
					String name = n.getName(locale);
					SelectItem item = new SelectItem(n, name);
					contractReportNames.add(item);			
				}
			}
		}
		return contractReportNames;
	}
	
	public List<SelectItem> getPersonReportNames() {
		if (personReportNames == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			personReportNames = new LinkedList<SelectItem>();
			for( ReportName n : ReportName.values() ) {
				if(n.getType()==ReportType.PERSON){
					String name = n.getName(locale);
					SelectItem item = new SelectItem(n, name);
					personReportNames.add(item);			
				}
			}
		}
		return personReportNames;
	}
	
}