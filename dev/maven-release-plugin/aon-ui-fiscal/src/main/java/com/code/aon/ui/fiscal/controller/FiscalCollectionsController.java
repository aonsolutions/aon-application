package com.code.aon.ui.fiscal.controller;


import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.file.tax.model.MOD340.MOD340Format;
import com.code.aon.fiscal.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Model347ReportOrder;
import com.code.aon.fiscal.enumeration.Model347Type;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.RentingStatus;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.fiscal.enumeration.VatType;

/**
 * Collections controller
 * 
 * @author Consulting & Development. Joseba Urkiri - 25-may-2006
 * 
 */
public class FiscalCollectionsController {

	private List<SelectItem> rentingStatuses;
	private List<SelectItem> vatTaxStatuses;
	private List<SelectItem> vatTaxDeclarationStatuses;
	private List<SelectItem> administrations;

	private List<SelectItem> vatTypes;
	private List<SelectItem> invoiceOrders;
	private List<SelectItem> periods;
	private List<SelectItem> model347Orders;
	private List<SelectItem> model347Types;
	private List<SelectItem> mod340Formats;

	public List<SelectItem> getRentingStatuses() {
		if (rentingStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			rentingStatuses = new LinkedList<SelectItem>();
			for (RentingStatus status:RentingStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				rentingStatuses.add(item);
			}
		}
		return rentingStatuses;
	}

	public List<SelectItem> getVatTaxStatuses() {
		if (vatTaxStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatTaxStatuses = new LinkedList<SelectItem>();
			for (VatTaxStatus status:VatTaxStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				vatTaxStatuses.add(item);
			}
		}
		return vatTaxStatuses;
	}

	public List<SelectItem> getVatTaxDeclarationStatuses() {
		if (vatTaxDeclarationStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatTaxDeclarationStatuses = new LinkedList<SelectItem>();
			for (VatTaxDeclarationStatus declarationStatus:VatTaxDeclarationStatus.values()) {
				String name = declarationStatus.getName(locale);
				SelectItem item = new SelectItem(declarationStatus, name);
				vatTaxDeclarationStatuses.add(item);
			}
		}
		return vatTaxDeclarationStatuses;
	}

	public List<SelectItem> getAdministrations() {
		if (administrations == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			administrations = new LinkedList<SelectItem>();
			for (Administration administration:Administration.values()) {
				String name = administration.getName(locale);
				SelectItem item = new SelectItem(administration, name);
				administrations.add(item);
			}
		}
		return administrations;
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

	public List<SelectItem> getInvoiceReportOrders() {
		if (invoiceOrders == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			invoiceOrders = new LinkedList<SelectItem>();
			for (InvoiceReportOrder order:InvoiceReportOrder.values()) {
				String name = order.getName(locale);
				SelectItem item = new SelectItem(order, name);
				invoiceOrders.add(item);
			}
		}
		return invoiceOrders;
	}

	public List<SelectItem> getPeriods() {
		if (periods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			periods = new LinkedList<SelectItem>();
			for (Period period:Period.values()) {
				String name = period.getName(locale);
				SelectItem item = new SelectItem(period, name);
				periods.add(item);
			}
		}
		return periods;
	}

	public List<SelectItem> getModel347ReportOrders() {
		if (model347Orders == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			model347Orders = new LinkedList<SelectItem>();
			for (Model347ReportOrder order:Model347ReportOrder.values()) {
				String name = order.getName(locale);
				SelectItem item = new SelectItem(order, name);
				model347Orders.add(item);
			}
		}
		return model347Orders;
	}
	
	public List<SelectItem> getModel347Types() {
		if (model347Types == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			model347Types = new LinkedList<SelectItem>();
			for (Model347Type type:Model347Type.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				model347Types.add(item);
			}
		}
		return model347Types;
	}
	
	public List<SelectItem> getMod340Formats() {
		if (mod340Formats == null) {
			mod340Formats = new LinkedList<SelectItem>();
			for (MOD340Format format:MOD340Format.values()) {
				String name = format.getDescription();
				SelectItem item = new SelectItem(format, name);
				mod340Formats.add(item);
			}
		}
		return mod340Formats;
	}
	
	
}