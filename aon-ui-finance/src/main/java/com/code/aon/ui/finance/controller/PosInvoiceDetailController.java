package com.code.aon.ui.finance.controller;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;

public class PosInvoiceDetailController extends SaleInvoiceDetailController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ArrayList<InvoiceDetail> checks = new ArrayList<InvoiceDetail>();

	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		clearCheckedDetails();
	}

	public void itemChanged(Item item) {
		super.itemChanged(item, true, true);
	}

	public void quantityChanged(double quantity) {
		super.quantityChanged(quantity);
	}

	public void discountChanged(String discount) {
		super.discountChanged(discount);
	}

	public void onPlusQuantity(ActionEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		if (invoiceDetail != null) {
			quantityChanged(invoiceDetail.getQuantity() + 1);
		}
	}

	public void onMinusQuantity(ActionEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		if (invoiceDetail != null) {
			quantityChanged(invoiceDetail.getQuantity() - 1);
		}
	}

	@Override
	public double getTaxableBase() {
		return ((InvoiceDetail)getTo()).getTaxableBase();
	}

	@Override
	public void onAccept(ActionEvent event) {
		fillTaxDataInDetail(true, true);
		super.onAccept(event);
		onReset(event);
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		InvoiceDetail to = (InvoiceDetail)model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			InvoiceDetail to = (InvoiceDetail)model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			InvoiceDetail to = (InvoiceDetail)model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<InvoiceDetail> getCheckedDetails() {
		return checks;
	}
	
	public void clearCheckedDetails() {
		checks = new ArrayList<InvoiceDetail>();
	}
	
	public int getCheckedCount() {
		return getCheckedDetails().size();
	}

}
