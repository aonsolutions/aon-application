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

public class NivelControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		resetCategoriaModel();
		resetPercepcionModel();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		resetCategoriaModel();
		resetPercepcionModel();
	}

	private void resetCategoriaModel() throws ControllerListenerException {
		LinesController categoriaController = (LinesController) AonUtil.getController(CATEGORIA_CONTROLLER_NAME);
		try {
			categoriaController.clearCriteria();
			Criteria criteria = categoriaController.getCriteria();
			criteria.addNullExpression(categoriaController.getFieldName(IPayrollAlias.CATEGORIA_NIVEL));
			categoriaController.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}		
	}
	
	private void resetPercepcionModel() throws ControllerListenerException {
		LinesController percepcionController = (LinesController) AonUtil.getController(PERCEPCION_CONTROLLER_NAME);
		try {
			percepcionController.clearCriteria();
			Criteria criteria = percepcionController.getCriteria();
			criteria.addNullExpression(percepcionController.getFieldName(IPayrollAlias.PERCNIV_NIVEL_ID_CDG));
			percepcionController.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}		
	}
	
}
