package com.code.aon.ui.finance.controller;

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
import com.code.aon.finance.BankConcept;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Collections controller
 * 
 * @author Consulting & Development. Joseba Urkiri - 25-may-2006
 * 
 */
public class FinanceCollectionsController {

	private List<SelectItem> billingPeriods;
	private List<SelectItem> creditorStatuses;
	private List<SelectItem> financeTrackingTypes;
	private List<SelectItem> financeBatchStatus;
	private List<SelectItem> financeBatchPaymentTypes;
	private List<SelectItem> financeBatchChargeTypes;
	private List<SelectItem> financeStatuses;
	private List<SelectItem> invoiceTypes;
	private List<SelectItem> invoiceStatuses;
	private List<SelectItem> statementConcepts;
	private List<SelectItem> statementStatuses;

	public List<SelectItem> getBillingPeriods() {
		if (billingPeriods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			billingPeriods = new LinkedList<SelectItem>();
			for( BillingPeriod period : BillingPeriod.values() ) {
				String name = period.getName(locale);
				SelectItem item = new SelectItem(period, name);
				billingPeriods.add(item);			
			}
		}
		return billingPeriods;
	}

	public List<SelectItem> getCreditorStatuses() {
		if (creditorStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			creditorStatuses = new LinkedList<SelectItem>();
			for (CreditorStatus status:CreditorStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				creditorStatuses.add(item);
			}
		}
		return creditorStatuses;
	}

	public List<SelectItem> getFinanceTrackingTypes() {
		if (financeTrackingTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeTrackingTypes = new LinkedList<SelectItem>();
			for (FinanceTrackingType type:FinanceTrackingType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				financeTrackingTypes.add(item);
			}
		}
		return financeTrackingTypes;
	}

	public List<SelectItem> getFinanceBatchStatus() {
		if (financeBatchStatus == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeBatchStatus = new LinkedList<SelectItem>();
			for (FinanceBatchStatus status:FinanceBatchStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				financeBatchStatus.add(item);
			}
		}
		return financeBatchStatus;
	}

	public List<SelectItem> getFinanceBatchPaymentTypes() {
		if (financeBatchPaymentTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeBatchPaymentTypes = new LinkedList<SelectItem>();
			for (FinanceBatchType type:FinanceBatchType.values()) {
				if (type.isPayment() == null || type.isPayment()) {
					String name = type.getName(locale);
					SelectItem item = new SelectItem(type, name);
					financeBatchPaymentTypes.add(item);
				}
			}
		}
		return financeBatchPaymentTypes;
	}

	public List<SelectItem> getFinanceBatchChargeTypes() {
		if (financeBatchChargeTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeBatchChargeTypes = new LinkedList<SelectItem>();
			for (FinanceBatchType type:FinanceBatchType.values()) {
				if (type.isPayment() == null || !type.isPayment()) {
					String name = type.getName(locale);
					SelectItem item = new SelectItem(type, name);
					financeBatchChargeTypes.add(item);
				}
			}
		}
		return financeBatchChargeTypes;
	}

	public List<SelectItem> getFinanceStatuses() {
		if (financeStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeStatuses = new LinkedList<SelectItem>();
			for (FinanceStatus status:FinanceStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				financeStatuses.add(item);
			}
		}
		return financeStatuses;
	}

	public List<SelectItem> getInvoiceTypes() {
		if (invoiceTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			invoiceTypes = new LinkedList<SelectItem>();
			for (InvoiceType type: InvoiceType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				invoiceTypes.add(item);
			}
		}
		return invoiceTypes;
	}

	public List<SelectItem> getInvoiceStatuses() {
		if (invoiceStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			invoiceStatuses = new LinkedList<SelectItem>();
			for (InvoiceStatus status:InvoiceStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				invoiceStatuses.add(item);
			}
		}
		return invoiceStatuses;
	}
	
	public List<SelectItem> getStatementConcepts() {
		if (statementConcepts == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			statementConcepts = new LinkedList<SelectItem>();
			for (StatementConcept concept:StatementConcept.values()) {
				String name = concept.getName(locale);
				SelectItem item = new SelectItem(concept, name);
				statementConcepts.add(item);
			}
		}
		return statementConcepts;
	}
	
	public List<SelectItem> getStatementStatuses() {
		if (statementStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			statementStatuses = new LinkedList<SelectItem>();
			for (StatementStatus status:StatementStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				statementStatuses.add(item);
			}
		}
		return statementStatuses;
	}
	
	public List<SelectItem> getBankConcepts() throws ManagerBeanException {
		List<SelectItem> bankConcepts = new LinkedList<SelectItem>();
		IManagerBean bankConceptBean = BeanManager.getManagerBean(BankConcept.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bankConceptBean.getFieldName(IEntityAlias.BANK_CONCEPT_NAME));
		Iterator<ITransferObject> iter = bankConceptBean.getList(criteria).iterator();
		while(iter.hasNext()){
			BankConcept bankConcept = (BankConcept)iter.next();
			SelectItem item = new SelectItem(bankConcept, bankConcept.getName());
			bankConcepts.add(item);
		}
		return bankConcepts;
	}		

}