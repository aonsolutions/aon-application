package com.code.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Cuentas;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.CuentaController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class CuentaControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub

		Integer cdg = ((Cliente)(FormUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getCdg();
		String desc = ((Cliente)(FormUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getDescripcion();
		((Cuentas)(event.getController().getTo())).getCliente().setCdg(cdg);
		((Cuentas)(event.getController().getTo())).getCliente().setDescripcion(desc);
		
		CuentaController controller = (CuentaController)FormUtil.getController(IPayrollConstants.CUENTA_CONTROLLER_NAME);
		
		 try {			 
			 
			 ((Cuentas)(event.getController().getTo())).setCdg(controller.getCode());
			 
			} catch (ManagerBeanException e) {
			}	
		
	}
	
	
	


}
