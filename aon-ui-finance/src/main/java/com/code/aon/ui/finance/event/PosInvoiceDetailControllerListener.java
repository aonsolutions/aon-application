package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PosInvoiceDetailControllerListener extends InvoiceDetailControllerListener {

	@Override
	@SuppressWarnings("unchecked")
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			InvoiceDetailController controller = (InvoiceDetailController)event.getController();
			Iterator<ITransferObject> iterator = ((List<ITransferObject>)controller.getModel().getWrappedData()).iterator();
			while (iterator.hasNext()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
				invoiceDetail.fillTaxDataInDetail();
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);

		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setQuantity(1);
		invoiceDetail.setTaxDataInDetail(true);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanSelected(event);

		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setTaxDataInDetail(true);
		invoiceDetail.fillTaxDataInDetail();
	}

}
