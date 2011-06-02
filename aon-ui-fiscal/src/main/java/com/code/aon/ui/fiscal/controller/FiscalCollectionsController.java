package com.code.aon.ui.fiscal.controller;


import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.file.tax.model.MOD340.MOD340Format;
import com.code.aon.file.tax.model.MOD347.MOD347Format;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.Mod347Type;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.RentingStatus;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.fiscal.enumeration.VatType;
import com.code.aon.fiscal.enumeration.WithholdingDetailKey;
import com.code.aon.fiscal.enumeration.WithholdingStatus;

public class FiscalCollectionsController {

	private List<SelectItem> rentingStatuses;

	private List<SelectItem> withholdingDetailKeys;

	private List<SelectItem> withholdingStatuses;

	private List<SelectItem> vatTaxStatuses;
	private List<SelectItem> vatTaxDeclarationStatuses;

	private List<SelectItem> vatTypes;
	private List<SelectItem> invoiceOrders;
	private List<SelectItem> periods;
	
	private List<SelectItem> mod347Formats;
	private List<SelectItem> mod347Types;

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

	public List<SelectItem> getWithholdingDetailKeys() {
		if (withholdingDetailKeys == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			withholdingDetailKeys = new LinkedList<SelectItem>();
			for (WithholdingDetailKey key:WithholdingDetailKey.values()) {
				String name = key.getName(locale);
				SelectItem item = new SelectItem(key, name);
				withholdingDetailKeys.add(item);
			}
		}
		return withholdingDetailKeys;
	}

	public List<SelectItem> getWithholdingStatuses() {
		if (withholdingStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			withholdingStatuses = new LinkedList<SelectItem>();
			for (WithholdingStatus status:WithholdingStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				withholdingStatuses.add(item);
			}
		}
		return withholdingStatuses;
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
	
	public List<SelectItem> getMod347Formats() {
		if (mod347Formats == null) {
			mod347Formats = new LinkedList<SelectItem>();
			for (MOD347Format format : MOD347Format.values()) {
				String name = format.getDescription();
				SelectItem item = new SelectItem(format, name);
				mod347Formats.add(item);
			}
		}
		return mod347Formats;
	}

	public List<SelectItem> getMod347Types() {
		if (mod347Types == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			mod347Types = new LinkedList<SelectItem>();
			for (Mod347Type type : Mod347Type.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				mod347Types.add(item);
			}
		}
		return mod347Types;
	}
	
}