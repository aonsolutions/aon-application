package com.code.aon.ui.finance.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;

public class InvoicingAddressController extends LinesController {

	@SuppressWarnings("unchecked")
	public void onAddress(ActionEvent event) throws ManagerBeanException{
		IController invoicingController = getMasterController();
		Invoice invoice = (Invoice)invoicingController.getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(this.getFieldName(IFinanceAlias.INVOICE_ADDRESS_INVOICE_ID), invoice.getId());
		Iterator iter = this.getManagerBean().getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			InvoiceAddress invoiceAddress = (InvoiceAddress)iter.next();
			this.setTo(invoiceAddress);
		}else{
			this.onReset(null);
			((InvoiceAddress)this.getTo()).setInvoice(invoice);
		}
	}
}