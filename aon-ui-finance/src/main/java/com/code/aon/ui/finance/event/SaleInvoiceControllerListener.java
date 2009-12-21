package com.code.aon.ui.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SaleInvoiceControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			SaleInvoiceController saleInvoiceController = (SaleInvoiceController)this.getController(); 
			saleInvoiceController.loadAddresses(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			SaleInvoiceController saleInvoiceController = (SaleInvoiceController)this.getController(); 
			Invoice invoice = (Invoice) saleInvoiceController.getTo();
			saleInvoiceController.loadAddresses(invoice.getRegistry().getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			Invoice invoice = (Invoice)event.getController().getTo();
			invoice.setType(InvoiceType.SALES);
			invoice.setStatus(InvoiceStatus.PENDING);
			fillTaxInfo(invoice);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			Invoice invoice = (Invoice)event.getController().getTo();
			fillTaxInfo(invoice);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	private void fillTaxInfo(Invoice invoice) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Customer customer = (Customer) customerBean.get(invoice.getRegistry().getId());

		invoice.setSurcharge((customer != null) ? customer.isSurcharge() : false);
		invoice.setTaxFree((customer != null) ? customer.isTaxFree() : false);
		invoice.setWithholding((customer != null) ? customer.isWithholding() : false);
	}

}