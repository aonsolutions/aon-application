package com.code.aon.ui.fiscal.controller;



import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.file.tax.model.MOD340.MOD340Format;
import com.code.aon.file.tax.model.MOD347.MOD347Format;
import com.code.aon.fiscal.enumeration.FiscalBatchType;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.InvoiceReportOrder;
import com.code.aon.fiscal.enumeration.Mod347Type;
import com.code.aon.fiscal.enumeration.Mod349Status;
import com.code.aon.fiscal.enumeration.Mod349Type;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.fiscal.enumeration.VatType;
import com.code.aon.fiscal.enumeration.WithholdingStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class FiscalCollectionsController {

	private List<SelectItem> withholdingStatuses;
	private List<SelectItem> vatTaxStatuses;
	private List<SelectItem> vatTaxDeclarationStatuses;
	private List<SelectItem> vatTypes;
	private List<SelectItem> invoiceOrders;
	private List<SelectItem> periods;
	private List<SelectItem> quarterPeriods;
	private List<SelectItem> mod347Formats;
	private List<SelectItem> mod347Types;
	private List<SelectItem> mod349Statuses;
	private List<SelectItem> mod349Types;	
	private List<SelectItem> mod340Formats;
	private List<SelectItem> fiscalBatchTypes;
	private List<SelectItem> fiscalModelStatuses;

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

	public List<SelectItem> getMod349Statuses() {
		if (mod349Statuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			mod349Statuses = new LinkedList<SelectItem>();
			for (Mod349Status  status:Mod349Status.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				mod349Statuses.add(item);
			}
		}
		return mod349Statuses;
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
	
	public List<SelectItem> getQuarterPeriods() {
		if (quarterPeriods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			quarterPeriods = new LinkedList<SelectItem>();
			quarterPeriods.add( new SelectItem(Period.T1, Period.T1.getName(locale)) );
			quarterPeriods.add( new SelectItem(Period.T2, Period.T2.getName(locale)) );
			quarterPeriods.add( new SelectItem(Period.T3, Period.T3.getName(locale)) );
			quarterPeriods.add( new SelectItem(Period.T4, Period.T4.getName(locale)) );
		}
		return quarterPeriods;
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
	
	public List<SelectItem> getFiscalBatchTypes() {
		if (fiscalBatchTypes == null) {
			fiscalBatchTypes = new LinkedList<SelectItem>();
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for (FiscalBatchType type:FiscalBatchType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				fiscalBatchTypes.add(item);
			}
		}
		return fiscalBatchTypes;
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

	public List<SelectItem> getMod349Types() {
		if (mod349Types == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			mod349Types = new LinkedList<SelectItem>();
			for (Mod349Type type : Mod349Type.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				mod349Types.add(item);
			}
		}
		return mod349Types;
	}
	
	public List<SelectItem> getFiscalModelStatuses() {
		if (fiscalModelStatuses == null) {
			fiscalModelStatuses = new LinkedList<SelectItem>();
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			for (FiscalModelStatus status : FiscalModelStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				fiscalModelStatuses.add(item);
			}
		}
		return fiscalModelStatuses;
	}
	
	public List<SelectItem> getModelPayMethods() throws ManagerBeanException {
		List<SelectItem> payMethods = new LinkedList<SelectItem>();
		IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(payMethodBean.getFieldName(IEntityAlias.PAY_METHOD_NAME));
		for (ITransferObject ito : payMethodBean.getList(criteria)) {
			PayMethod pMethod = (PayMethod)ito;
			if (pMethod.getType() == PayMethodType.NEGOTIABLE_DOCUMENT ||
				pMethod.getType() == PayMethodType.CASH_BASIS ) {
				SelectItem item = new SelectItem(pMethod, pMethod.getName());
				payMethods.add(item);
			}
		}
		return payMethods;
	}

	public FiscalModelType getModel111() {
		return FiscalModelType.M111;
	}
	public FiscalModelType getModel115() {
		return FiscalModelType.M115;
	}
	public FiscalModelType getModel123() {
		return FiscalModelType.M123;
	}
	public FiscalModelType getModel130() {
		return FiscalModelType.M130;
	}
	public FiscalModelType getModel310() {
		return FiscalModelType.M310;
	}
	public FiscalModelType getModel311() {
		return FiscalModelType.M311;
	}
	public FiscalModelType getModel131() {
		return FiscalModelType.M131;
	}

}