package com.code.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.util.AonUtil;

public class ConveniosColectivosControllerListener extends ControllerAdapter implements IPayrollConstants {

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		resetNivelModel();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		resetNivelModel();
	}

	private void resetNivelModel() throws ControllerListenerException {
		LinesController nivelController = (LinesController) AonUtil.getController(NIVEL_CONTROLLER_NAME);
		try {
			nivelController.clearCriteria();
			Criteria criteria = nivelController.getCriteria();
			criteria.addNullExpression(nivelController.getFieldName(IPayrollAlias.NIVEL_CONVENIO_CDG));
			nivelController.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}		
	}
	
}
