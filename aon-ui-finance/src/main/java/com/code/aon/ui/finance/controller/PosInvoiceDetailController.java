package com.code.aon.ui.finance.controller;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.InvoiceDetail;

public class PosInvoiceDetailController extends SaleInvoiceDetailController {

	public double getSalesPrice() {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		return CommonUtil.round(invoiceDetail.getPrice() * (1 + invoiceDetail.getVatPercent() / 100 - invoiceDetail.getRetentionPercent() / 100));
	}

	public double getTotalSalesPrice() {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		double taxableBase = getPriceStrategy().getBasePrice(invoiceDetail);
		return CommonUtil.round(taxableBase * (1 + invoiceDetail.getVatPercent() / 100 - invoiceDetail.getRetentionPercent() / 100));
	}

}
