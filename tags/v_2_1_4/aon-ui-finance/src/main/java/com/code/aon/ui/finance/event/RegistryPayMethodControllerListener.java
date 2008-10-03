package com.code.aon.ui.finance.event;

import com.code.aon.finance.RegistryPayMethod;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryPayMethodControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryPayMethod rPayMethod = (RegistryPayMethod)event.getController().getTo();
		rPayMethod.setDaysBetweenPayments(0);
		rPayMethod.setDaysToFirstPayment(0);
		rPayMethod.setNumberOfPayments(1);
	}
}
