package com.code.aon.ui.finance.event;

import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

@Deprecated
public class InvoicingAddressListener extends LinesControllerListener {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		getLinesController().initModel();
		super.updateDetailCriteria(event.getController(), true);
		getLinesController().initializeModel();
	}

}