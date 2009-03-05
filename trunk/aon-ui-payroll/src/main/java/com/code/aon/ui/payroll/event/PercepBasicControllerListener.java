package com.code.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;

import com.code.aon.payroll.principales.persona.Embargo;
import com.code.aon.payroll.principales.personas.Otrperc;
import com.code.aon.payroll.principales.personas.Percep;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ConveniosComplementoController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.NominaexController;
import com.code.aon.ui.payroll.controller.OtrpercepController;
import com.code.aon.ui.payroll.controller.PercepBasicController;
import com.code.aon.ui.payroll.controller.PercepController;
import com.code.aon.ui.payroll.controller.Utils;

public class PercepBasicControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		PercepBasicController controller = (PercepBasicController) event
				.getController();
		try {

			controller.refreshComplementos();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		PercepBasicController controller = (PercepBasicController) event
				.getController();
		try {

			controller.refreshComplementos();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {

		((PercepBasicController) getController()).generarNumero(null);

	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		int numero = ((Percep)this.getController().getTo()).getTrabajador().getCdg();
		int cdg = Integer.parseInt(Utils.maxCode("Percep", "id.cdg", "id.numero="+numero));
		
		((Percep)this.getController().getTo()).getId().setCdg(cdg+1);
		((Percep)this.getController().getTo()).getId().setNumero(numero);
		
	}
}
