package com.esferalia.aon.ui.payroll.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.payroll.enumeration.ContractCalendarEventType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractDuration;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractOption;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.ContractWorkingDay;
import com.esferalia.aon.payroll.enumeration.DischargeCause;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.SalaryTemplate;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class PayrollCollectionsController {

	private List<SelectItem> contractDurations;
	private List<SelectItem> contractWorkingDays;
	private List<SelectItem> contractCalendarEventTypes;
	private List<SelectItem> paymentTypes;
	private List<SelectItem> deductionTypes;
	private List<SelectItem> salaryTypes;
	
	private List<SelectItem> contractCodes;
	private Map<ContractType,List<SelectItem>> contractCodesMap;
	private List<SelectItem> contractModels;
	private List<SelectItem> contractOptions;
	private Map<ContractOption,List<SelectItem>> contractTypesMap;
	private List<SelectItem> quoteGroups;
	private List<SelectItem> ssRegimes;
	private List<SelectItem> salaryTemplates;
	
	private List<SelectItem> streetTypes;
	private List<SelectItem> leaveReportTypes;
	private List<SelectItem> leaveTypes;
	private List<SelectItem> dischargeCauses;
	
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
	
	public List<SelectItem> getSalaryTypes() {
		if (salaryTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			salaryTypes = new LinkedList<SelectItem>();
			for( SalaryType salaryType : SalaryType.values() ) {
				String name = salaryType.getName(locale);
				SelectItem item = new SelectItem(salaryType, name);
				salaryTypes.add(item);			
			}
		}
		return salaryTypes;
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
	
	public Map<ContractType,List<SelectItem>> getContractCodesMap() {
		if (contractCodesMap == null) {
			contractCodesMap = new HashMap<ContractType, List<SelectItem>>();
			List<SelectItem> list = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(null, "-");
			list.add(item);
			contractCodesMap.put(null, list);
			for (ContractType contractType:ContractType.values()) {
				list = new LinkedList<SelectItem>();
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				for (ContractCode c : contractType.getCodes()) {
					String name = c.getName(locale);
					item = new SelectItem(c, StringUtils.abbreviate(name,100));
					list.add(item);
				}
				contractCodesMap.put(contractType, list);
			}
		}
		return contractCodesMap;
	}
	
	public Map<ContractOption, List<SelectItem>> getContractTypesMap() {
		if (contractTypesMap == null) {
			contractTypesMap = new HashMap<ContractOption, List<SelectItem>>();
			List<SelectItem> list = new LinkedList<SelectItem>();
			SelectItem item = new SelectItem(null, "-");
			list.add(item);
			contractTypesMap.put(null, list);
			for (ContractOption contractOption:ContractOption.values()) {
				list = new LinkedList<SelectItem>();
				if(contractOption!=null){
					Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
					for (ContractType t : contractOption.getTypes()) {
						String name = t.getName(locale);
						item = new SelectItem(t, StringUtils.abbreviate(name,100));
						list.add(item);
					}
				}
				contractTypesMap.put(contractOption, list);
			}
		}
		return contractTypesMap;
	}

	public List<SelectItem> getStreetTypes() {
		if (streetTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
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
	
	public List<SelectItem> getSsRegimes() {
		if (ssRegimes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			ssRegimes = new LinkedList<SelectItem>();
			SSRegimeType[] models = SSRegimeType.values();
			for (SSRegimeType ss : models) {
				String name = ss.getName(locale);
				SelectItem item = new SelectItem(ss, name);
				ssRegimes.add(item);
			}
		}
		return ssRegimes;
	}
	
	public List<SelectItem> getLeaveReportTypes() {
		if (leaveReportTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			leaveReportTypes = new LinkedList<SelectItem>();
			LeaveReportType[] reports = LeaveReportType.values();
			for (LeaveReportType r : reports) {
				String name = r.getName(locale);
				SelectItem item = new SelectItem(r, name);
				leaveReportTypes.add(item);
			}
		}
		return leaveReportTypes;
	}
	
	public List<SelectItem> getLeaveTypes() {
		if (leaveTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			leaveTypes = new LinkedList<SelectItem>();
			LeaveType[] leaves = LeaveType.values();
			for (LeaveType l : leaves) {
				String name = l.getName(locale);
				SelectItem item = new SelectItem(l, name);
				leaveTypes.add(item);
			}
		}
		return leaveTypes;
	}
	
	public List<SelectItem> getDischargeCauses() {
		if (dischargeCauses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
			.getLocale();
			dischargeCauses = new LinkedList<SelectItem>();
			DischargeCause[] causes = DischargeCause.values();
			for (DischargeCause l : causes) {
				String name = l.getName(locale);
				SelectItem item = new SelectItem(l, name);
				dischargeCauses.add(item);
			}
		}
		return dischargeCauses;
	}
		
}