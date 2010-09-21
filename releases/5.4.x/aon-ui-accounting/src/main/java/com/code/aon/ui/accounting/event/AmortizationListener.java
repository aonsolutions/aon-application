package com.code.aon.ui.accounting.event;

import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.ui.accounting.controller.AmortizationDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AmortizationListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Amortization a = (Amortization) event.getController().getTo();
		a.setFeePeriod(AmortizationPeriod.YEARLY);
	}
	

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Amortization a = (Amortization) event.getController().getTo();
		checkCancellationDate( a );
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		AmortizationDetailController ad = (AmortizationDetailController) AonUtil.getRegisteredBean("amortizationDetail");
		ad.initModel();
	}
	
	private void checkCancellationDate(Amortization a) throws ControllerListenerException {
		if (a.getDeadline() != null && a.getSaleAmount() == null) {
			throw new ControllerListenerException("Si se indica una fecha de baja, se debe indicar un importe de venta.");
		}
		if (a.getDeadline() == null && a.getSaleAmount() != null) {
			throw new ControllerListenerException("Si se indica un importe de venta, se debe indicar una fecha de baja.");
		}
	}
}
