package com.code.aon.ui.accounting.event;

import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.ui.accounting.controller.amortization.AmortizationController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AmortizationListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Amortization a = (Amortization) event.getController().getTo();
		a.setFeePeriod(AmortizationPeriod.YEARLY);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AmortizationController ac = (AmortizationController) event.getController();
		ac.onCalculate(null);
	}
	
}
