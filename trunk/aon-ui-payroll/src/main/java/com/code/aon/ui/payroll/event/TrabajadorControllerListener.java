package com.code.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class TrabajadorControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
			
		super.afterBeanCreated(event);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {

		super.beforeBeanAdded(event);
	}
	
	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println(" beforeBeanSelected ");

		super.beforeBeanSelected(event);
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		
		resetTrabajoModel();
		super.beforeModelInitialized(event);
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		
		resetTrabajoModel();
		super.afterBeanCanceled(event);
	}
	
	
	private void resetTrabajoModel() throws ControllerListenerException {
		/*
		LinesController trabajoController = (LinesController) FormUtil.getController(IPayrollConstants.TRABAJO_CONTROLLER_NAME);
		try {
			trabajoController.clearCriteria();
			Criteria criteria = trabajoController.getCriteria();
			criteria.addNullExpression(trabajoController.getFieldName(IPayrollAlias.TRABAJO_EMPRPER_CDG));
			trabajoController.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}
		*/		
	}
}
