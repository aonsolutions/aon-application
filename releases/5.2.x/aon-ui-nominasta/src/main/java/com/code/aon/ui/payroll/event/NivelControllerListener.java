package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.convenios.Nivel;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ConveniosColectivoController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class NivelControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		resetCategoriaModel();
		resetPercepcionModel();
	}
	
	@Override
	public void beforeBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		resetCategoriaModel();
		resetPercepcionModel();
	}
	 
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ConveniosColectivoController convenioController = (ConveniosColectivoController)FormUtil.getController(IPayrollConstants.CONVENIO_CONTROLLER_NAME);
		
		((Nivel)(event.getController().getTo())).setConvenio(((Convenio)convenioController.getTo()));
		((Nivel)(event.getController().getTo())).getId().setCodcon(((Convenio)convenioController.getTo()).getCdg());
		
	}

	private void resetCategoriaModel() throws ControllerListenerException {
		LinesController categoriaController = (LinesController) FormUtil.getController(CATEGORIA_CONTROLLER_NAME);
		try {
			categoriaController.clearCriteria();
			Criteria criteria = categoriaController.getCriteria();
			criteria.addNullExpression(categoriaController.getFieldName(IPayrollAlias.CATEGORIA_NIVEL_ID_CDG));
			categoriaController.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}		
	}
	
	private void resetPercepcionModel() throws ControllerListenerException {
		LinesController percepcionController = (LinesController) FormUtil.getController(PERCNIV_CONTROLLER_NAME);
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
