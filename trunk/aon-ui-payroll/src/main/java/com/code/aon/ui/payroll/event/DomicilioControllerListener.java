package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.convenios.Nivel;
import com.code.aon.payroll.auxiliares.convenios.Percepcion;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.util.AonUtil;

public class DomicilioControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub

		Integer cdg = ((Cliente)(AonUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getCdg();
		String desc = ((Cliente)(AonUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getDescripcion();
		((Domicilio)(event.getController().getTo())).getCliente().setCdg(cdg);
		((Domicilio)(event.getController().getTo())).getCliente().setDescripcion(desc);
	}
	
	
	


}
