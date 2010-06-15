package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.auxiliares.contratos.ContratosTc2;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ContratosTc2ControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		digitControl(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		digitControl(event);
	}
	
	private void digitControl(ControllerEvent event){
		IController c = event.getController();
		ContratosTc2 n = (ContratosTc2) c.getTo();
		String cdg = n.getCdg();

		if (cdg.length() == 2)
			n.setCdg("0" + cdg);
		if (cdg.length() == 1)
			n.setCdg("00" + cdg);
	}
}
