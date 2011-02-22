package com.esferalia.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.payroll.enumeration.ContractCalendarEventType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractDuration;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.payroll.enumeration.SalaryTemplate;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class PayrollCollectionsController {

	private List<SelectItem> contractDurations;
	private List<SelectItem> contractWorkingDays;
	private List<SelectItem> contractCalendarEventTypes;
	private List<SelectItem> paymentTypes;
	private List<SelectItem> deductionTypes;
	
	private List<SelectItem> contractCodes;
	private List<SelectItem> contractModels;
	private List<SelectItem> contractOptions;
	private List<SelectItem> quoteGroups;
	private List<SelectItem> salaryTemplates;
	
	private List<SelectItem> streetTypes;
	
	public List<SelectItem> getPaymentTypes() {
		if (paymentTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			paymentTypes = new LinkedList<SelectItem>();
			for( PaymentType paymentType : PaymentType.values() ) {
				String name = paymentType.getName(locale);
				SelectItem item = new SelectItem(paymentType, name);
				paymentTypes.add(item);			
			}
		}
		return paymentTypes;
	}
	
	public List<SelectItem> getDeductionTypes() {
		if (deductionTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			deductionTypes = new LinkedList<SelectItem>();
			for( DeductionType deductionType : DeductionType.values() ) {
				String name = deductionType.getName(locale);
				SelectItem item = new SelectItem(deductionType, name);
				deductionTypes.add(item);			
			}
		}
		return deductionTypes;
	}
	
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
	
	public List<SelectItem> getContractCalendarEventTypes() {
		if (contractCalendarEventTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractCalendarEventTypes = new LinkedList<SelectItem>();
			for( ContractCalendarEventType type : ContractCalendarEventType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				contractCalendarEventTypes.add(item);			
			}
		}
		return contractCalendarEventTypes;
	}
	
	public List<SelectItem> getContractOptions() {
		if (contractOptions == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractOptions = new LinkedList<SelectItem>();
			ContractOption[] options = ContractOption.values();
			for (ContractOption o : options) {
				String name = o.getName(locale);
				SelectItem item = new SelectItem(o, name);
				contractOptions.add(item);
			}
		}
		return contractOptions;
	}
	
	public List<SelectItem> getContractCodes() {
		if (contractCodes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			contractCodes = new LinkedList<SelectItem>();
			ContractCode[] codes = ContractCode.values();
			for (ContractCode cc : codes) {
				String name = cc.getName(locale);
				SelectItem item = new SelectItem(cc, name);
				contractCodes.add(item);
			}
		}
		return contractCodes;
	}
	
	public List<SelectItem> getContractModels() {
		if (contractModels == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			contractModels = new LinkedList<SelectItem>();
			ContractModel[] models = ContractModel.values();
			for (ContractModel cm : models) {
				String name = cm.getDescription(locale);
				SelectItem item = new SelectItem(cm, name);
				contractModels.add(item);
			}
		}
		return contractModels;
	}
	
	public List<SelectItem> getStreetTypes() {
		if (streetTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			streetTypes = new LinkedList<SelectItem>();
			StreetType[] models = StreetType.values();
			for (StreetType cm : models) {
				String name = cm.getName(locale);
				SelectItem item = new SelectItem(cm, name);
				streetTypes.add(item);
			}
		}
		return streetTypes;
	}
	
	public List<SelectItem> getQuoteGroups() {
		if (quoteGroups == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			quoteGroups = new LinkedList<SelectItem>();
			QuoteGroup[] models = QuoteGroup.values();
			for (QuoteGroup cm : models) {
				String name = cm.getFullName(locale);
				SelectItem item = new SelectItem(cm, name);
				quoteGroups.add(item);
			}
		}
		return quoteGroups;
	}
	
	public List<SelectItem> getSalaryTemplates() {
		if (salaryTemplates == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			salaryTemplates = new LinkedList<SelectItem>();
			SalaryTemplate[] templates = SalaryTemplate.values();
			for (SalaryTemplate t : templates) {
				String name = t.getName(locale);
				SelectItem item = new SelectItem(t, name);
				salaryTemplates.add(item);
			}
		}
		return salaryTemplates;
	}
	
	
}