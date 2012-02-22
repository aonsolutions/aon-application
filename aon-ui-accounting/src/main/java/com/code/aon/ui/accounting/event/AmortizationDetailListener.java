package com.code.aon.ui.accounting.event;

import javax.faces.event.AbortProcessingException;

import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.controller.amortization.AmortizationDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AmortizationDetailListener extends ControllerAdapter {

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		AmortizationDetail detail = (AmortizationDetail) event.getController().getTo();
		detail.setCoefficient(CommonUtil.round(detail.getAllocation() * 100 / detail.getAmortization().getAmount()));
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		prepareForRefresh(event);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		prepareForRefresh(event);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		prepareForRefresh(event);
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		prepareForRefresh(event);
	}

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		AmortizationDetailController adc = (AmortizationDetailController) event.getController();
		adc.setSummaryModel(null);
	}
	
	private void prepareForRefresh(ControllerEvent event) {
		try {
			AmortizationDetailController adc = (AmortizationDetailController) event.getController();
			adc.forceRefresh();
		} catch (ManagerBeanException e) {
			String msg = "Imposible actualizar los totales de la lista.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
}
