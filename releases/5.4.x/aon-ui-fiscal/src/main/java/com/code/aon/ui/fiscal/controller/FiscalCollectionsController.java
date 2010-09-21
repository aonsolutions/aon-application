package com.code.aon.ui.fiscal.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.fiscal.enumeration.Model347ReportOrder;
import com.code.aon.fiscal.enumeration.Model347Type;
import com.code.aon.fiscal.enumeration.VatPeriod;
import com.code.aon.fiscal.enumeration.VatReportOrder;
import com.code.aon.fiscal.enumeration.VatType;

/**
 * Collections controller
 * 
 * @author Consulting & Development. Joseba Urkiri - 25-may-2006
 * 
 */
public class FiscalCollectionsController {

	private List<SelectItem> vatTypes;
	private List<SelectItem> vatOrders;
	private List<SelectItem> vatPeriods;
	private List<SelectItem> model347Orders;
	private List<SelectItem> model347Types;

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

	public List<SelectItem> getVatPeriods() {
		if (vatPeriods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			vatPeriods = new LinkedList<SelectItem>();
			for (VatPeriod period:VatPeriod.values()) {
				String name = period.getName(locale);
				SelectItem item = new SelectItem(period, name);
				vatPeriods.add(item);
			}
		}
		return vatPeriods;
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
	
}