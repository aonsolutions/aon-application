package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.VatReportOrder;
import com.code.aon.finance.enumeration.VatType;

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
	private List<SelectItem> financeBatchTypes;
	private List<SelectItem> financeStatuses;
	private List<SelectItem> vatTypes;
	private List<SelectItem> vatOrders;
	private List<SelectItem> invoiceTypes;
	private List<SelectItem> invoiceStatuses;

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
			for (FinanceBatchStatus type:FinanceBatchStatus.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				financeBatchStatus.add(item);
			}
		}
		return financeBatchStatus;
	}

	public List<SelectItem> getFinanceBatchTypes() {
		if (financeBatchTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeBatchTypes = new LinkedList<SelectItem>();
			for (FinanceBatchType type:FinanceBatchType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				financeBatchTypes.add(item);
			}
		}
		return financeBatchTypes;
	}

	public List<SelectItem> getFinanceStatuses() {
		if (financeStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			financeStatuses = new LinkedList<SelectItem>();
			for (FinanceStatus type:FinanceStatus.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				financeStatuses.add(item);
			}
		}
		return financeStatuses;
	}

	public List<SelectItem> getVatTypes() {
		if (vatTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatTypes = new LinkedList<SelectItem>();
			for (VatType type:VatType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				vatTypes.add(item);
			}
		}
		return vatTypes;
	}

	public List<SelectItem> getVatReportOrders() {
		if (vatOrders == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatOrders = new LinkedList<SelectItem>();
			for (VatReportOrder order:VatReportOrder.values()) {
				String name = order.getName(locale);
				SelectItem item = new SelectItem(order, name);
				vatOrders.add(item);
			}
		}
		return vatOrders;
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
			for (InvoiceStatus type:InvoiceStatus.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				invoiceStatuses.add(item);
			}
		}
		return invoiceStatuses;
	}

}