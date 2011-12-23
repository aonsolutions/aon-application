package com.code.aon.ui.company.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.enumeration.EnterpriseSalaryTemplate;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.company.enumeration.SalarySendingMethod;
import com.code.aon.company.enumeration.SalaryTemplate;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyCollectionsController {

	private List<SelectItem> salarySendingMethods;
	private List<SelectItem> enterpriseSalaryTemplates;
	private List<SelectItem> salaryTemplates;
	private List<SelectItem> saleInvoiceTemplates;
	private List<SelectItem> reportPrintOptions;
	private List<SelectItem> simpleReportPrintOptions;
	
	public List<SelectItem> getShortReportPrintOptions() {
		if (simpleReportPrintOptions == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			simpleReportPrintOptions = new LinkedList<SelectItem>();
			ReportPrintOption[] list = ReportPrintOption.values();
			for (ReportPrintOption o : list) {
				if(o!=ReportPrintOption.LEFT_SIDE){
					String name = o.getName(locale);
					SelectItem item = new SelectItem(o, name);
					simpleReportPrintOptions.add(item);
				}
			}
		}
		return simpleReportPrintOptions;
	}
	
	public List<SelectItem> getReportPrintOptions() {
		if (reportPrintOptions == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			reportPrintOptions = new LinkedList<SelectItem>();
			ReportPrintOption[] list = ReportPrintOption.values();
			for (ReportPrintOption o : list) {
				String name = o.getName(locale);
				SelectItem item = new SelectItem(o, name);
				reportPrintOptions.add(item);
			}
		}
		return reportPrintOptions;
	}
	
	public List<SelectItem> getSaleInvoiceTemplates() {
		if (saleInvoiceTemplates == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			saleInvoiceTemplates = new LinkedList<SelectItem>();
			SaleInvoiceTemplate[] st = SaleInvoiceTemplate.values();
			for (SaleInvoiceTemplate c : st) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c, name);
				saleInvoiceTemplates.add(item);
			}
		}
		return saleInvoiceTemplates;
	}
	
	public List<SelectItem> getSalaryTemplates() {
		if (salaryTemplates == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			salaryTemplates = new LinkedList<SelectItem>();
			SalaryTemplate[] st = SalaryTemplate.values();
			for (SalaryTemplate c : st) {
				String name = c.getName(locale);
				SelectItem item = new SelectItem(c.getValue(), name);
				salaryTemplates.add(item);
			}
		}
		return salaryTemplates;
	}
	
	public List<SelectItem> getSalarySendingMethods() {
		if (salarySendingMethods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			salarySendingMethods = new LinkedList<SelectItem>();
			for( SalarySendingMethod type : SalarySendingMethod.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type.name(), name);
				salarySendingMethods.add(item);			
			}
		}
		return salarySendingMethods;
	}	
	
	public List<SelectItem> getEnterpriseSalaryTemplates() {
		if (enterpriseSalaryTemplates == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			enterpriseSalaryTemplates = new LinkedList<SelectItem>();
			for( EnterpriseSalaryTemplate type : EnterpriseSalaryTemplate.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type.getValue(), name);
				enterpriseSalaryTemplates.add(item);			
			}
		}
		return enterpriseSalaryTemplates;
	}	
	
    public List<SelectItem> getCompanyAddresses() throws ManagerBeanException {
    	LinkedList<SelectItem> addresses = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), company.getId());
    		criteria.addOrder(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS));
    		List<ITransferObject> list = registryAddressBean.getList(criteria);
    		for (ITransferObject to : list) {
    			RegistryAddress rAddress = (RegistryAddress)to;
    			addresses.add(new SelectItem(rAddress, rAddress.getShortAddress()));
    		}
    	}
    	return addresses;
    }

    public List<SelectItem> getAllCompanyBanks() throws ManagerBeanException {
    	LinkedList<SelectItem> banks = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean registryBankBean = BeanManager.getManagerBean(RegistryBank.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), company.getId());
    		criteria.addOrder(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_ID));
    		List<ITransferObject> list = registryBankBean.getList(criteria);
    		for (ITransferObject to : list) {
    			RegistryBank rBank = (RegistryBank)to;
    			banks.add(new SelectItem(rBank, rBank.getFullName()));
    		}
    	}
    	return banks;
    }

    public List<SelectItem> getActiveCompanyBanks() throws ManagerBeanException {
    	LinkedList<SelectItem> banks = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean registryBankBean = BeanManager.getManagerBean(RegistryBank.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), company.getId());
    		criteria.addEqualExpression(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_ACTIVE), true);
    		criteria.addOrder(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_ID));
    		List<ITransferObject> list = registryBankBean.getList(criteria);
    		for (ITransferObject to : list) {
    			RegistryBank rBank = (RegistryBank)to;
    			banks.add(new SelectItem(rBank, rBank.getFullName()));
    		}
    	}
    	return banks;
    }

    public WorkPlace getWorkPlace() {
		return null;
	}

	public void setWorkPlace( WorkPlace workPlace ) {
	}

	public List<SelectItem> getWorkPlaces() throws ManagerBeanException {
		List<SelectItem> workPlaces = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), company.getId());
    		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), true);
    		criteria.addOrder(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ID));
    		List<ITransferObject> list = workPlaceBean.getList(criteria);
    		for (ITransferObject to : list) {
    			WorkPlace workPlace = (WorkPlace)to;
    			workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
    		}
		}
		return workPlaces;
	}	

	public int getWorkPlacesCount() throws ManagerBeanException {
		int workPlacesCount = 0;
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator<?> iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), company.getId());
    		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), true);
    		workPlacesCount = workPlaceBean.getCount(criteria);
		}
		return workPlacesCount;
	}
	
}