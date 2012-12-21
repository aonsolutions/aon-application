package com.code.aon.ui.finance.event;

import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class InvoiceDetailLinesListener extends LinesControllerListener {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);
		getLinesController().onReset(null);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		LinesController childController = getLinesController();
		childController.setModel(null);
		if (childController.getTo() != null) {
			childController.onAccept(null);
		}
		super.afterBeanAdded(event);
	}

}
