package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ClienteController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;


public class ClienteControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub


		
		ClienteController controller = (ClienteController)FormUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME);
		
		 try {			 
			 
			 ((Cliente)(event.getController().getTo())).setCdg(controller.getCode());
			 ((ClienteController)getController()).setDefaultFields();
			} catch (ManagerBeanException e) {
			}	
		
		
	
	
	
	}
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
	
		ClienteController controller = (ClienteController)FormUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME);
		
	
		
		
		
	}
	
	


	
@Override
public void beforeBeanAdded(ControllerEvent event)
		throws ControllerListenerException {
	
	ClienteController controller = (ClienteController)FormUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME);
	
	 try {			 
		
		 ((Cliente)(event.getController().getTo())).setCdg(controller.getCode());
	     ((ClienteController)getController()).verifyNullFields();

		} catch (ManagerBeanException e) {
		}	
	

}
}
