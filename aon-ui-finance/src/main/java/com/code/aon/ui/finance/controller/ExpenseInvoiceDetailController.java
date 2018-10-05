package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
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

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(item.getFullName());
		invoiceDetail.setQuantity(1);
		fillTaxDataInDetail(false, true);

		if (invoice.getRegistry() == null || invoice.getRegistry().getId() == null) {
			Creditor creditor = obtainExpenseLastCreditor(item);
			if (creditor != null) {
				((ExpenseInvoiceController)getMasterController()).creditorChanged(creditor);
			}
		}
		
		if(this.isNevv() && item.getProduct().isComposition()) {
			this.getCompositeHandler().load(this, item);
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
		invoiceDetail.setRetentionQuota((getInvoice().isWithholding()) ? getRetentionQuota(invoiceDetail) : 0);
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
				taxableBase = CommonUtil.ceil(getTotalChanged() / ( 1 + vatPercent / 100 - retentionPercent / 100), i);
				vatQuota = getQuota(taxableBase, vatPercent);
				retentionQuota = getQuota(taxableBase, retentionPercent);
				if (getTotalChanged() == getTotal(taxableBase, vatQuota, retentionQuota)) {
					break;
				} else {
					taxableBase = CommonUtil.floor(getTotalChanged() / ( 1 + vatPercent / 100 - retentionPercent / 100), i);
					vatQuota = getQuota(taxableBase, vatPercent);
					retentionQuota = getQuota(taxableBase, retentionPercent);
					if (getTotalChanged() == getTotal(taxableBase, vatQuota, retentionQuota)) {
						break;
					} else if (i < 4) {
						taxableBase = CommonUtil.round(taxableBase + 5 / Math.pow(10, i+1), i+1);
						vatQuota = getQuota(taxableBase, vatPercent);
						retentionQuota = getQuota(taxableBase, retentionPercent);
						if (getTotalChanged() == getTotal(taxableBase, vatQuota, retentionQuota)) {
							break;
						}
					}
				}
			}
		}

		invoiceDetail.setTaxableBase(taxableBase);
		invoiceDetail.setVatQuota(vatQuota);
		invoiceDetail.setRetentionQuota((getInvoice().isWithholding()) ? retentionQuota : 0);
	}

	public double getInvoiceDetailTotal() throws ManagerBeanException {
		return getInvoiceDetailTotal((Invoice)getMasterController().getTo(), (InvoiceDetail)this.getModel().getRowData());
	}

	public double getToInvoiceDetailTotal() {
		return getInvoiceDetailTotal((Invoice)getMasterController().getTo(), (InvoiceDetail)this.getTo());
	}
	public void setToInvoiceDetailTotal(double toInvoiceDetailTotal) {
	}

	private double getInvoiceDetailTotal(Invoice invoice, InvoiceDetail invoiceDetail) {
		double vatQuota = (!invoice.isVatFree()) ? invoiceDetail.getVatQuota() : 0;
		double retentionQuota = (!invoice.isRetentionFree()) ? invoiceDetail.getRetentionQuota() : 0;
		return getTotal(invoiceDetail.getTaxableBase(), vatQuota, retentionQuota);
	}

	private double getTotal(double taxableBase, double vatQuota, double retentionQuota) {
		return CommonUtil.round(taxableBase + vatQuota - retentionQuota, 4);
	}

}
