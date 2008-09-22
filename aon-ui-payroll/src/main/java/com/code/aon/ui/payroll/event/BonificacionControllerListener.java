package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.cotizacion.Bonificacion;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;


public class BonificacionControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		IController c = event.getController();
		Bonificacion bon = (Bonificacion) c.getTo();
		
		if (bon.getCdg()==0) {
			throw new ControllerListenerException( "Código reservado. Por favor, introduzca otro." );
		}
	}

}
