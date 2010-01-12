package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.DomicilioController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;


public class DomicilioControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub

		Integer cdg = ((Cliente)(FormUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getCdg();
		String desc = ((Cliente)(FormUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getDescripcion();
		((Domicilio)(event.getController().getTo())).getCliente().setCdg(cdg);
		((Domicilio)(event.getController().getTo())).getCliente().setDescripcion(desc);
		
		DomicilioController controller = (DomicilioController)FormUtil.getController(IPayrollConstants.DOMICILIO_CONTROLLER_NAME);
		
		 try {			 
			 
			 ((Domicilio)(event.getController().getTo())).setCdg(controller.getCode());
			 
			} catch (ManagerBeanException e) {
			}	
		
		
	
	
	
	}
@Override
public void beforeBeanAdded(ControllerEvent event)
		throws ControllerListenerException {
	
	DomicilioController controller = (DomicilioController)FormUtil.getController(IPayrollConstants.DOMICILIO_CONTROLLER_NAME);
	
	 try {			 
		
		 ((Domicilio)(event.getController().getTo())).setCdg(controller.getCode());
		 
		} catch (ManagerBeanException e) {
		}	
	

}
}
