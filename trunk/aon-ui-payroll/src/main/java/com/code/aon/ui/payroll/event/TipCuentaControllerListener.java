package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.payroll.principales.empresa.Emprlban;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.DomicilioController;
import com.code.aon.ui.payroll.controller.TipCuentaController;
import com.code.aon.ui.payroll.controller.TipDomicilioController;
import com.code.aon.ui.util.AonUtil;


public class TipCuentaControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

@Override
public void beforeBeanAdded(ControllerEvent event)
		throws ControllerListenerException {
	
	TipCuentaController controller = (TipCuentaController)AonUtil.getController(IPayrollConstants.TIPCUENTA_CONTROLLER_NAME);
	
	 try {			 
		
		 ((Emprlban)(event.getController().getTo())).setCdg(controller.getCode());
		 
		} catch (ManagerBeanException e) {
		}	
	

}
}
