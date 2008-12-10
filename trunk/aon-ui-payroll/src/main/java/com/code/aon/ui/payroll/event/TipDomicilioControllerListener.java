package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.TipDomicilioController;


public class TipDomicilioControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

@Override
public void beforeBeanAdded(ControllerEvent event)
		throws ControllerListenerException {
	
	TipDomicilioController controller = (TipDomicilioController)FormUtil.getController(IPayrollConstants.TIPDOMICILIO_CONTROLLER_NAME);
	
	 try {			 
		
		 ((Emprdom)(event.getController().getTo())).setCdg(controller.getCode());
		 
		} catch (ManagerBeanException e) {
		}	
	

}
}
