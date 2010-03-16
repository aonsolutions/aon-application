package com.code.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ContratosInternosController;
import com.code.aon.payroll.auxiliares.contratos.ContratosInternos;

public class ContratosInternosControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ContratosInternosController controller = (ContratosInternosController) event.getController();
		try {
			controller.refreshAsimilados();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ContratosInternosController controller = (ContratosInternosController) event.getController();
		try {
			controller.refreshAsimilados();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		IController c = event.getController();
		ContratosInternos n = (ContratosInternos) c.getTo();
		String cdg = n.getCdg();

		if (cdg.length() == 1)
			n.setCdg("0" + cdg);
	}

}
