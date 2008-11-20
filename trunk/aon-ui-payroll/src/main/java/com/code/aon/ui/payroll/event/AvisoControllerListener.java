package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.payroll.principales.Avisos;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
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
		
		super.afterEditSearch(event);
	}
	

	/*	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub

		Integer cdg = ((Cliente)(AonUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getCdg();
		String desc = ((Cliente)(AonUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getDescripcion();
		((Avisos)(event.getController().getTo())).getCliente().setCdg(cdg);
		((Avisos)(event.getController().getTo())).getCliente().setDescripcion(desc);
		
		DomicilioController controller = (DomicilioController)AonUtil.getController("Domicilio");
		
		 try {
			 System.out.println("------------" + controller.getCode());
			 ((Domicilio)(event.getController().getTo())).setCdg(controller.getCode());
			 
			} catch (ManagerBeanException e) {
			}	

	
	
	}	*/

}
