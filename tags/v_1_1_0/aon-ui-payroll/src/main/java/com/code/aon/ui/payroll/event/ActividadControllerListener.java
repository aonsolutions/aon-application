package com.code.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ActividadController;
import com.code.aon.ui.payroll.controller.EmpreactController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class ActividadControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
((ActividadController)getController()).generateCdg();
	}		

@Override
public void beforeBeanAdded(ControllerEvent event)
		throws ControllerListenerException {
	
	
	((ActividadController)getController()).generateCdg();

}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		resetDomiciliosModel();
		resetCuentasModel();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		resetDomiciliosModel();
		resetCuentasModel();
	}

	private void resetDomiciliosModel() throws ControllerListenerException {
		LinesController tipDomicilioController = (LinesController) FormUtil.getController(TIPDOMICILIO_CONTROLLER_NAME);
		try {
			tipDomicilioController.clearCriteria();
			Criteria criteria = tipDomicilioController.getCriteria();
			criteria.addNullExpression(tipDomicilioController.getFieldName(IPayrollAlias.EMPRDOM_ACTIVIDAD_CDG));
			tipDomicilioController.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}		
	}
	
	private void resetCuentasModel() throws ControllerListenerException {
		LinesController tipCuentaController = (LinesController) FormUtil.getController(TIPCUENTA_CONTROLLER_NAME);
		try {
			tipCuentaController.clearCriteria();
			Criteria criteria = tipCuentaController.getCriteria();
			criteria.addNullExpression(tipCuentaController.getFieldName(IPayrollAlias.EMPRLBAN_ACTIVIDAD_CDG));
			tipCuentaController.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}		
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		
		EmpreactController act = (EmpreactController)FormUtil.getController(IPayrollConstants.EMPREACT_CONTROLLER_NAME);
		try {
			act.setModel(this.getController().getModel());
			act.onSelect(null);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
