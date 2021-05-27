package com.code.aon.ui.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class InvoiceDetailLinesListener extends LinesControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetailController detailController = (InvoiceDetailController)getLinesController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)detailController.getTo();
		if (invoiceDetail.getItem() == null || invoiceDetail.getItem().getId() == null) {
			detailController.onCancel(null);
		}

		super.afterBeanAdded(event);
	}

}
