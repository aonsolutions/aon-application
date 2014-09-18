package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;

public class PurchaseInvoiceDetailController extends InvoiceDetailController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			itemChanged(item);
		}
	}	

	public void itemChanged(Item item) {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(item.getFullName());
		if (invoiceDetail.getQuantity() == 0) {
			invoiceDetail.setQuantity(1);
		}
		Supplier supplier = null;
		if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
			try {
				supplier = (Supplier)BeanManager.getManagerBean(Supplier.class).get(invoice.getRegistry().getId());
			} catch (ManagerBeanException e) {
			}
		}
		invoiceDetail.setPrice(getPriceStrategy().getUnitPurchasePrice(invoiceDetail, invoice.getIssueDate(), supplier));
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		//Implementar cuando se creen Tarifas de Compra para Proveedores, mientras tanto no se utiliza.
	}

}
