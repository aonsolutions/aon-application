package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.ui.common.components.LookupChangeEvent;

public class SaleInvoiceDetailController extends InvoiceDetailController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			itemChanged(item);
		} else {
			InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
			invoiceDetail.setQuantity(0);
			invoiceDetail.setPrice(0);
			invoiceDetail.getDiscountExpression().setDiscountExpr("0.0");
		}
	}	

	public void itemChanged(Item item) {
		itemChanged(item, false, true);
	}

	public void itemChanged(Item item, boolean workWithSalesPrice, boolean includeQuotas) {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(item.getFullName());
		if (invoiceDetail.getQuantity() == 0) {
			invoiceDetail.setQuantity(1);
		}

		Customer customer = null;
		if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
			try {
				customer = (Customer)BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
			} catch (ManagerBeanException e) {
			}
		}
		if (item.getProduct().getType() == ProductType.INCREASE) {
			invoiceDetail.getDiscountExpression().setDiscountExpr(Double.toString(item.getProfitPercent()));
		}
		invoiceDetail.setPrice(getPriceStrategy().getUnitPrice(invoiceDetail, invoice.getIssueDate(), customer));
		fillTaxDataInDetail(workWithSalesPrice, includeQuotas);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		quantityChanged((event.getNewValue() != null && !event.getNewValue().toString().equals("")) ? (Double)event.getNewValue() : 0);
		fillTaxDataInDetail(false, true);
	}

	public void quantityChanged(double quantity) {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setQuantity(quantity);
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getId() != null) {
			Customer customer = null;
			if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
				try {
					customer = (Customer)BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
				} catch (ManagerBeanException e) {
				}
			}
			invoiceDetail.setPrice(getPriceStrategy().getUnitPrice(invoiceDetail, invoice.getIssueDate(), customer));
		} else {
			invoiceDetail.setPrice(0);
		}
	}

	public void onPriceChanged(ValueChangeEvent event) {
		priceChanged((event.getNewValue() != null && !event.getNewValue().toString().equals("")) ? (Double)event.getNewValue() : 0);
		fillTaxDataInDetail(false, true);
	}

	public void priceChanged(double price) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setPrice(price);
	}

	public void onDiscountChanged(ValueChangeEvent event) {
		discountChanged((event.getNewValue() != null && !event.getNewValue().toString().equals("")) ? event.getNewValue().toString() : "0.0");
		fillTaxDataInDetail(false, true);
	}

	public void discountChanged(String discount) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.getDiscountExpression().setDiscountExpr(discount);
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		salesPriceChanged((event.getNewValue() != null && !event.getNewValue().toString().equals("")) ? (Double)event.getNewValue() : 0);
	}

	public void salesPriceChanged(double salesPrice) {
		ItemPricesManager pricesManager = new ItemPricesManager();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setPrice(pricesManager.getPrice(invoiceDetail.getVatPercent(), invoiceDetail.getRetentionPercent(), salesPrice, 4));
	}

}
