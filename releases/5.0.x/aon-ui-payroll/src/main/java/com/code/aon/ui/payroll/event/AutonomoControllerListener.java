package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.principales.autonomos.Autonomos;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.AutonomoController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;


public class AutonomoControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub


		
		AutonomoController controller = (AutonomoController)FormUtil.getController(IPayrollConstants.AUTONOMO_CONTROLLER_NAME);
		
		 try {			 
			 
			 ((Autonomos)(event.getController().getTo())).setCdg(controller.getCode());
			 ((AutonomoController)getController()).setDefaultFields();
			} catch (ManagerBeanException e) {
			}	
		
		
	
	
	
	}
@Override
public void beforeBeanAdded(ControllerEvent event)
		throws ControllerListenerException {
	
	
	AutonomoController controller = (AutonomoController)FormUtil.getController(IPayrollConstants.AUTONOMO_CONTROLLER_NAME);
	
	 try {			 
		 
		 ((Autonomos)(event.getController().getTo())).setCdg(controller.getCode());
	     ((AutonomoController)getController()).verifyNullFields();

		 
		} catch (ManagerBeanException e) {
		}	
	
	
	

}
}
