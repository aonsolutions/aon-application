package com.code.aon.ui.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.finance.controller.UndeductibleInvoiceController;
import com.code.aon.ui.finance.controller.UndeductibleInvoiceDetailController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class UndeductibleInvoiceDetailControllerListener extends InvoiceDetailControllerListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);

		UndeductibleInvoiceDetailController controller = (UndeductibleInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		try {
			InvoiceDetail lastDetail = ((UndeductibleInvoiceController)controller.getMasterController()).obtainCreditorLastExpense(((InvoiceDetail)controller.getTo()).getLine());
			if (lastDetail != null) {
				controller.itemChanged(lastDetail.getItem());
				invoiceDetail.setTaxableBase(lastDetail.getTaxableBase());
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanAdded(event);

		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(invoiceDetail.getTaxableBase());
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanUpdated(event);

		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(invoiceDetail.getTaxableBase());
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
	}

}
