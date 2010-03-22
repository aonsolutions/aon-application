package com.code.aon.ui.finance.event;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.finance.controller.UndeductibleInvoiceDetailController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class UndeductibleInvoiceDetailControllerListener extends InvoiceDetailControllerListener {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);

		UndeductibleInvoiceDetailController controller = (UndeductibleInvoiceDetailController)event.getController();
		try {
			controller.loadExpenseItems();
			if (controller.getExpenseItems().size() > 0) {
				SelectItem selectItem = (SelectItem)controller.getExpenseItems().get(0);
				Item item = (Item)selectItem.getValue();
				controller.itemChanged(item);
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		UndeductibleInvoiceDetailController controller = (UndeductibleInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		try {
			controller.loadExpenseItems();
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		controller.setLongDescription(!StringUtils.equals(invoiceDetail.getItem().getProduct().getName(), invoiceDetail.getDescription()));
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(invoiceDetail.getTaxableBase());
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setQuantity(1);
		invoiceDetail.setPrice(invoiceDetail.getTaxableBase());
		invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
	}

}
