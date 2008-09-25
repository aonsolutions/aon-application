package com.code.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ContratosInternos;

public class ContratosInternosControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ContratosInternos controller = (ContratosInternos) event.getController();
		try {
			controller.refreshAsimilados();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ContratosInternos controller = (ContratosInternos) event.getController();
		try {
			controller.refreshAsimilados();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
