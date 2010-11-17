package com.code.aon.ui.employee.event;

import com.code.aon.ui.employee.controller.SalaryPaymentController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SalaryPaymentControllerListener extends ControllerAdapter{

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		resetPayments(event);
	}
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		resetPayments(event);
	}
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		resetPayments(event);
	}
	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		resetPayments(event);
	}
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		resetPayments(event);
	}
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		resetPayments(event);
	}
	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		resetPayments(event);
	}
	private void resetPayments(ControllerEvent event) {
		SalaryPaymentController c = (SalaryPaymentController) event.getController();
		c.setPayments(null);
	}

}
