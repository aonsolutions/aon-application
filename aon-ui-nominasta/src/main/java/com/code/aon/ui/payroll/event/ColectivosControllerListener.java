package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.auxiliares.Colectivos;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ColectivosControllerListener extends ControllerAdapter {

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
		Colectivos n = (Colectivos) c.getTo();
		String cdg = n.getCdg();

		if (cdg.length() == 3)
			n.setCdg("0" + cdg);
		if (cdg.length() == 2)
			n.setCdg("00" + cdg);
		if (cdg.length() == 1)
			n.setCdg("000" + cdg);
	}
}
