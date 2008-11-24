package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.payroll.principales.Avisos;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Cuentas;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.AvisoController;
import com.code.aon.ui.payroll.controller.CuentaController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.DomicilioController;
import com.code.aon.ui.util.AonUtil;


public class AvisoControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	@Override
	public void afterEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		
		
		Integer cdg = ((Cliente)(AonUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getCdg();
		String desc = ((Cliente)(AonUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getDescripcion();
		((Avisos)(event.getController().getTo())).getCliente().setCdg(cdg);
		((Avisos)(event.getController().getTo())).getCliente().setDescripcion(desc);
		
		
	}
	

		
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub

		Integer cdg = ((Cliente)(AonUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getCdg();
		String desc = ((Cliente)(AonUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getDescripcion();
		((Avisos)(event.getController().getTo())).getCliente().setCdg(cdg);
		((Avisos)(event.getController().getTo())).getCliente().setDescripcion(desc);
		AvisoController controller = (AvisoController)AonUtil.getController(IPayrollConstants.AVISOS_CONTROLLER_NAME);
		
		
     try {			 
			 
			 ((Avisos)(event.getController().getTo())).setCdg(controller.getCode());
			 
			} catch (ManagerBeanException e) {
			}	
		
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		AvisoController controller = (AvisoController)AonUtil.getController(IPayrollConstants.AVISOS_CONTROLLER_NAME);
		
		
	     try {			 
				 
				 ((Avisos)(event.getController().getTo())).setCdg(controller.getCode());
				 
				} catch (ManagerBeanException e) {
				}	
			
		

	}
	
	
}
