package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.esferalia.aon.entity.IEntityAlias;

public class ExpenseInvoiceDetailController extends InvoiceDetailController {

	private double totalChanged;

	public double getTotalChanged() {
		return totalChanged;
	}
	public void setTotalChanged(double totalChanged) {
		this.totalChanged = totalChanged;
	}

	public void onItemChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			itemChanged(item);
		}
	}	

	public void itemChanged(Item item) throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		Tax vat = item.getProduct().getVat();
		Tax retention = item.getProduct().getRetention();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(item.getProduct().getName() + (item.getDetail() !=null ? " " + item.getDetail() : ""));
		invoiceDetail.setQuantity(1);
		if (vat == null || invoiceDetail.getVatPercent() != vat.getPercentage()) {
			invoiceDetail.setVatPercent(vat != null ? getTaxPercent(vat, invoice.getIssueDate(), false) : 0);
			invoiceDetail.setVatQuota(getVatQuota(invoiceDetail));
		}
		if (retention == null || invoiceDetail.getRetentionPercent() != retention.getPercentage()) {
			invoiceDetail.setRetentionPercent(retention != null ? getTaxPercent(retention, invoice.getIssueDate(), false) : 0);
			invoiceDetail.setRetentionQuota(getRetentionQuota(invoiceDetail));
		}

		if (invoice.getRegistry() == null || invoice.getRegistry().getId() == null) {
			((ExpenseInvoiceController)getMasterController()).creditorChanged(obtainExpenseLastCreditor(item));
		}
	}

	private Creditor obtainExpenseLastCreditor(Item item) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_ID), item.getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_TYPE), InvoiceType.EXPENSES);
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ISSUE_DATE), false);
		criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), false);
		for (ITransferObject ito : invoiceDetailBean.getList(criteria)) {
			Registry creditor = ((InvoiceDetail)ito).getInvoice().getRegistry();
			return (Creditor)BeanManager.getManagerBean(Creditor.class).get(creditor.getId());
		}
		return null;
	}

	public void onTaxableBaseChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
			invoiceDetail.setTaxableBase(((Double)event.getNewValue()).doubleValue());
			taxableBaseChanged(invoiceDetail);
		}
	}

	public void taxableBaseChanged(InvoiceDetail invoiceDetail) {
		invoiceDetail.setVatQuota(getVatQuota(invoiceDetail));
		invoiceDetail.setRetentionQuota(getRetentionQuota(invoiceDetail));
	}

	public void onVatQuotaChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
			double vatQuota = ((Double)event.getNewValue()).doubleValue();
			if (vatQuota != 0 && invoiceDetail.getVatPercent() == 0 && invoiceDetail.getItem() != null) {
				double vatPercent = invoiceDetail.getItem().getProduct().getVat().getPercentage();
				invoiceDetail.setVatPercent(vatPercent);
			}
		}
	}

	public void onRetentionQuotaChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
			double retentionQuota = ((Double)event.getNewValue()).doubleValue();
			if (retentionQuota != 0 && invoiceDetail.getRetentionPercent() == 0 && invoiceDetail.getItem() != null) {
				Tax retention = invoiceDetail.getItem().getProduct().getRetention();
				double retentionPercent = (retention != null) ? retention.getPercentage() : 0;
				invoiceDetail.setRetentionPercent(retentionPercent);
			}
		}
	}

	public void onTotalChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
			setTotalChanged(((Double)event.getNewValue()).doubleValue());
			totalChanged(invoiceDetail);
		}
	}

	public void totalChanged(InvoiceDetail invoiceDetail) {
		double taxableBase = 0;
		double vatQuota = 0;
		double retentionQuota = 0;
		double vatPercent = invoiceDetail.getVatPercent();
		double retentionPercent = invoiceDetail.getRetentionPercent();

		for (int i=2; i<=4; i++) {
			taxableBase = CommonUtil.round(getTotalChanged() / ( 1 + vatPercent / 100 - retentionPercent / 100), i);
			vatQuota = getQuota(taxableBase, vatPercent);
			retentionQuota = getQuota(taxableBase, retentionPercent);
			if (getTotalChanged() == getTotal(taxableBase, vatQuota, retentionQuota)) {
				break;
			} else {
				taxableBase = CommonUtil.truncate(getTotalChanged() / ( 1 + vatPercent / 100 - retentionPercent / 100), i);
				vatQuota = getQuota(taxableBase, vatPercent);
				retentionQuota = getQuota(taxableBase, retentionPercent);
				if (getTotalChanged() == getTotal(taxableBase, vatQuota, retentionQuota)) {
					break;
				}
			}
		}

		invoiceDetail.setTaxableBase(taxableBase);
		invoiceDetail.setVatQuota(vatQuota);
		invoiceDetail.setRetentionQuota(retentionQuota);
	}

	public double getVatQuota(InvoiceDetail invoiceDetail) {
		return getQuota(invoiceDetail.getTaxableBase(), invoiceDetail.getVatPercent());
	}

	public double getRetentionQuota(InvoiceDetail invoiceDetail) {
		return getQuota(invoiceDetail.getTaxableBase(), invoiceDetail.getRetentionPercent());
	}

	private double getQuota(double base, double percent) {
		return CommonUtil.round(base * percent / 100);
	}

	public double getInvoiceDetailTotal() throws ManagerBeanException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)this.getModel().getRowData();
		return getTotal(invoiceDetail.getTaxableBase(), invoiceDetail.getVatQuota(), invoiceDetail.getRetentionQuota());
	}

	public double getToInvoiceDetailTotal() {
		InvoiceDetail invoiceDetail = (InvoiceDetail)this.getTo();
		return getTotal(invoiceDetail.getTaxableBase(), invoiceDetail.getVatQuota(), invoiceDetail.getRetentionQuota());
	}

	public void setToInvoiceDetailTotal(double toInvoiceDetailTotal) {
	}

	private double getTotal(double taxableBase, double vatQuota, double retentionQuota) {
		return CommonUtil.round(taxableBase + vatQuota - retentionQuota, 4);
	}

}
