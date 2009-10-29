package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Company;
import com.code.aon.finance.PayMethod;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.PayMethodType;
import com.code.aon.ql.Criteria;

/**
 * Collections controller
 * 
 * @author Consulting & Development. Joseba Urkiri - 25-may-2006
 * 
 */
public class FinanceCollectionsController {

	/**
	 * Returns Security Level collection.
	 * 
	 * @return Security Level collection.
	 */
	public List<SelectItem> getSecurityLevels() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> levels = new LinkedList<SelectItem>();
		for (SecurityLevel level : SecurityLevel.values()) {
			String name = level.getName(locale);
			SelectItem item = new SelectItem(level, name);
			levels.add(item);
		}
		return levels;
	}

	/**
	 * Fills and Returns PayMethod collection
	 * 
	 * @return PayMethod collection
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getPayMethods() throws ManagerBeanException {
		List<SelectItem> payMethods = new LinkedList<SelectItem>();
		IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(payMethodBean.getFieldName(IFinanceAlias.PAY_METHOD_NAME));
		Iterator iter = payMethodBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			PayMethod pMethod = (PayMethod) iter.next();
			SelectItem item = new SelectItem(pMethod.getId(), pMethod.getName());
			payMethods.add(item);
		}
		return payMethods;
	}
	
	/**
	 * Returns a list of PayMethodType
	 * 
	 * @return list of PayMethodType
	 */
	public List<SelectItem> getPayMethodTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		PayMethodType[] payMethodType = PayMethodType.values();
		for (int i = 0; i < payMethodType.length; i++) {
			PayMethodType type = payMethodType[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
	}
	
	/**
	 * Returns a list of InvoiceStatus
	 * 
	 * @return list of InvoiceStatus
	 */
	public List<SelectItem> getInvoiceStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> statuses = new LinkedList<SelectItem>();
		InvoiceStatus[] invoiceStatus = InvoiceStatus.values();
		for (int i = 0; i < invoiceStatus.length; i++) {
			InvoiceStatus type = invoiceStatus[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			statuses.add(item);
		}
		return statuses;
	}
	
	/**
	 * Returns a list of InvoiceTypes
	 * 
	 * @return list of InvoiceTypes
	 */
	public List<SelectItem> getInvoiceTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		InvoiceType[] invoiceTypes = InvoiceType.values();
		for (int i = 0; i < invoiceTypes.length; i++) {
			InvoiceType type = invoiceTypes[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
	}
	
	/**
	 * Returns a list of FinanceStatus
	 * 
	 * @return list of FinanceStatus
	 */
	public List<SelectItem> getFinanceStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> statuses = new LinkedList<SelectItem>();
		FinanceStatus[] financeStatus = FinanceStatus.values();
		for (int i = 0; i < financeStatus.length; i++) {
			FinanceStatus type = financeStatus[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			statuses.add(item);
		}
		return statuses;
	}
	
	public List<SelectItem> getFinanceBatchTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		FinanceBatchType[] financeBatchTypes = FinanceBatchType.values();
		for (int i = 0; i < financeBatchTypes.length; i++) {
			FinanceBatchType type = financeBatchTypes[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			types.add(item);
		}
		return types;
	}
	
	public List<SelectItem> getFinanceBatchStatus() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> statuses = new LinkedList<SelectItem>();
		FinanceBatchStatus[] financeBatchStatuses = FinanceBatchStatus.values();
		for (int i = 0; i < financeBatchStatuses.length; i++) {
			FinanceBatchStatus type = financeBatchStatuses[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			statuses.add(item);
		}
		return statuses;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getCompanyBanks() throws ManagerBeanException{
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
    	Iterator iter = companyBean.getList(null).iterator();
    	LinkedList<SelectItem> rBanks = new LinkedList<SelectItem>();
    	if(iter.hasNext()){
    		Company company = (Company)iter.next();
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_REGISTRY_ID), company.getId());
    		iter = rBankBean.getList(criteria).iterator();
    		while(iter.hasNext()){
    			RegistryBank rBank = (RegistryBank)iter.next();
    			SelectItem item = new SelectItem(rBank.getId(),rBank.getBank().getName());
    			rBanks.add(item);
    		}
    	}
    	return rBanks;
    }
	
	public List<SelectItem> getFinanceTrackingTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> statuses = new LinkedList<SelectItem>();
		FinanceTrackingType[] financeTrackingTypes = FinanceTrackingType.values();
		for (int i = 0; i < financeTrackingTypes.length; i++) {
			FinanceTrackingType type = financeTrackingTypes[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			statuses.add(item);
		}
		return statuses;
	}
	
	public List<SelectItem> getCreditorStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> statuses = new LinkedList<SelectItem>();
		CreditorStatus[] creditorStatuses = CreditorStatus.values();
		for (int i = 0; i < creditorStatuses.length; i++) {
			CreditorStatus status = creditorStatuses[i];
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			statuses.add(item);
		}
		return statuses;
	}
}