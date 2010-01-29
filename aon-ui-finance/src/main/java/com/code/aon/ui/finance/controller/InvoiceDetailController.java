package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
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

}
