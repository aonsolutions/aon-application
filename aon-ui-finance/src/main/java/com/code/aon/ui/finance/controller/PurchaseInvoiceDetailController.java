package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;

public class PurchaseInvoiceDetailController extends InvoiceDetailController {

	public void onItemChanged(LookupChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			invoiceDetail.setItem(item);
			invoiceDetail.setDescription(item.getProduct().getName() + " " + (item.getDetail()!=null?item.getDetail():""));
			price = item.getPurchasePrice();
		}
		invoiceDetail.setPrice(price);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
	}

	public String getLineSourceInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		/*InvoiceDetail invoiceDetail = (InvoiceDetail)this.getModel().getRowData();
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
		}*/
		return info.toString();
	}

}
