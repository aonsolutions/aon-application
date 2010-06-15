package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.empresa.Emprlban;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ActividadController;
import com.code.aon.ui.payroll.controller.ClienteController;
import com.code.aon.ui.payroll.controller.EmpresaController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.TipCuentaController;


public class TipCuentaControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

@Override
public void beforeBeanAdded(ControllerEvent event)
		throws ControllerListenerException {
	
	TipCuentaController controller = (TipCuentaController)FormUtil.getController(IPayrollConstants.TIPCUENTA_CONTROLLER_NAME);
	ActividadController controller2 = (ActividadController)FormUtil.getController(IPayrollConstants.ACTIVIDAD_CONTROLLER_NAME);
	EmpresaController controller3 = (EmpresaController)FormUtil.getController(IPayrollConstants.EMPRESA_CONTROLLER_NAME);
	ClienteController controller4 = (ClienteController)FormUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME);

	 try {			 
		
		 ((Emprlban)(event.getController().getTo())).setCdg(controller.getCode());
		 ((Emprlban)(event.getController().getTo())).setActividad(((Actividad)controller2.getTo()));
		 ((Emprlban)(event.getController().getTo())).setEmpresa(((Empresa)controller3.getTo()));
		 ((Emprlban)(event.getController().getTo())).setCliente(((Cliente)controller4.getTo()));
		
		 
		} catch (ManagerBeanException e) {
		}	
	

}
}
