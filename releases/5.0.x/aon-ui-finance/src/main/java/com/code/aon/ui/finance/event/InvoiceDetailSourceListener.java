package com.code.aon.ui.finance.event;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceDetailSourceListener extends ControllerAdapter {

	private InvoiceSource source;

	public void setSource(String sourceName) {
		source = InvoiceSource.valueOf(sourceName);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setSource(source);
	}

}