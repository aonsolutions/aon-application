package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class InvoiceDetailController extends LinesController {

	private boolean longDescription;
	private IPriceStrategy priceStrategy;

	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		if (StringUtils.equals(invoiceDetail.getItem().getProduct().getName().trim(), invoiceDetail.getDescription().trim())) {
			String longDescription = invoiceDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				invoiceDetail.setDescription(invoiceDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public boolean isEditable() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)this.getModel().getRowData();
			InvoiceSource source = invoiceDetail.getSource();
			if (source == InvoiceSource.DELIVERY || source == InvoiceSource.INCOME || source == InvoiceSource.OFFER) {
				return false;
			}
		}
		return true;
	}	

	public double getTaxableBase() {
		return getPriceStrategy().getBasePrice((ICalculable)getTo());
	}

	public double getVatPercent() throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null && item.getProduct().getVat() != null && item.getProduct().getVat().getId() != null) {
			return getTaxPercent(item.getProduct().getVat(), invoice.getIssueDate(), false);
		}
		return 0;
	}

	public double getSurchargePercent() throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null && item.getProduct().getVat() != null && item.getProduct().getVat().getId() != null) {
			return getTaxPercent(item.getProduct().getVat(), invoice.getIssueDate(), true);
		}
		return 0;
	}

	public double getRetentionPercent() throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null && item.getProduct().getRetention() != null && item.getProduct().getRetention().getId() != null) {
			return getTaxPercent(item.getProduct().getRetention(), invoice.getIssueDate(), false);
		}
		return 0;
	}

	public double getTaxPercent(Tax tax, Date taxDate, boolean surcharge) throws ManagerBeanException {
		double percent = 0;
		if (!tax.getStartDate().after(taxDate)) {
			percent = surcharge ? tax.getSurcharge() : tax.getPercentage();
		} else {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_TAX_ID), tax.getId());
			criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_START_DATE), taxDate);
			criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_END_DATE), taxDate);
			Iterator<?> iterator = taxDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				TaxDetail taxDetail = (TaxDetail)iterator.next();
				percent = surcharge ? taxDetail.getSurcharge() : taxDetail.getValue();
			}
		}
		return percent;
	}

}
