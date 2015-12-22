package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.esferalia.aon.entity.IEntityAlias;

public class UndeductibleInvoiceDetailController extends InvoiceDetailController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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

		if (invoice.getRegistry() == null || invoice.getRegistry().getId() == null) {
			Creditor creditor = obtainExpenseLastCreditor(item);
			if (creditor != null) {
				((UndeductibleInvoiceController)getMasterController()).creditorChanged(creditor);
			}
		}
	}

	private Creditor obtainExpenseLastCreditor(Item item) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_ID), item.getId());
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_TYPE), InvoiceType.UNDEDUCTIBLE);
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
		}
	}

}
