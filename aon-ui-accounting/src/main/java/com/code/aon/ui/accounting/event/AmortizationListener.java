package com.code.aon.ui.accounting.event;

import com.code.aon.AonVersion;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.amortization.AmortizationController;
import com.code.aon.ui.accounting.controller.amortization.AmortizationDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AmortizationListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Amortization a = (Amortization) event.getController().getTo();
		a.setFeePeriod(AmortizationPeriod.YEARLY);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
		try {
			AmortizationController ac = (AmortizationController) event.getController();
			ac.calculate();
			AmortizationDetailController ad = (AmortizationDetailController) AonUtil.getRegisteredBean(IAccountingConstants.AMORTIZATION_DETAIL_CONTROLLER);
			ad.initModel();
			ad.onSearch(null);
			ac.resetInvoices();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			AmortizationController ac = (AmortizationController) event.getController();
			ac.resetInvoices();
			ac.synchronize();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	
}
