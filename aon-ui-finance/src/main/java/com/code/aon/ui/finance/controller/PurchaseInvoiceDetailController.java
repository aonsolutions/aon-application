package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.DeliveryDetail;

public class PurchaseInvoiceDetailController extends LinesController {

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

	public void onItemChanged(LookupChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			invoiceDetail.setItem(item);
			invoiceDetail.setDescription(item.getProduct().getName() + " " + (item.getDetail()!=null?item.getDetail():""));

			/*Date date = invoiceDetail.getInvoice().getIssueDate();
			Tariff tariff;
			try {
				SaleInvoiceController master = (SaleInvoiceController) getMasterController();
				Customer customer = master.getCustomer();
				tariff = customer.getTariff();
			} catch (ManagerBeanException e) {
				tariff = null;
			}
			price = getPriceStrategy().getUnitPrice(invoiceDetail, date, tariff);*/
		}
		invoiceDetail.setPrice(price);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getId() != null) {
			double price = 0;
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				invoiceDetail.setQuantity((Double)event.getNewValue());
	
				/*Date date = invoiceDetail.getInvoice().getIssueDate();
				Tariff tariff;
				try {
					SaleInvoiceController master = (SaleInvoiceController) getMasterController();
					Customer customer = master.getCustomer();
					tariff = customer.getTariff();
				} catch (ManagerBeanException e) {
					tariff = null;
				}
				price = getPriceStrategy().getUnitPrice(invoiceDetail, date, tariff);*/
			}
			invoiceDetail.setPrice(price);
		}
	}

	public double getTaxableBase() {
		return getPriceStrategy().getBasePrice((ICalculable)getTo());
	}

	public String getLineSourceInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		InvoiceDetail invoiceDetail = (InvoiceDetail)this.getModel().getRowData();
		if (!isEditable() && invoiceDetail.getSourceId() != null) {
			String message = "";
			String refCode = "";
			int line = 0;
			if (invoiceDetail.getSource() == InvoiceSource.OFFER) {
				IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
				OfferDetail offerDetail = (OfferDetail)offerDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage("commercialBundle", "commercial_offer");
				refCode = offerDetail.getOffer().getReferenceCode();
				line = offerDetail.getLine().intValue();
			} else if (invoiceDetail.getSource() == InvoiceSource.DELIVERY) {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage("financeBundle", "finance_invoice_delivery");
				refCode = deliveryDetail.getDelivery().getReferenceCode();
				line = deliveryDetail.getLine().intValue();
			}

			info.append(AonUtil.getMessage("financeBundle", "finance_source"));
			info.append(" ");
			info.append(message);
			info.append(" ");
			info.append(refCode);
			info.append(" - ");
			info.append(AonUtil.getMessage("financeBundle", "finance_invoice_detail_line"));
			info.append(" ");
			info.append(line);
		}
		return info.toString();
	}

}
