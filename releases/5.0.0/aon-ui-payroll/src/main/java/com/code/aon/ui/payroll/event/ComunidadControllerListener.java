package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.geograficas.Comunidad;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class ComunidadControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Comunidad c = (Comunidad)event.getController().getTo();
		
		if (c.getCdg().length()==1)
			c.setCdg("0"+c.getCdg());
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		resetProvinciaModel();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		resetProvinciaModel();
	}

	private void resetProvinciaModel() throws ControllerListenerException {
		LinesController provinciaController = (LinesController) FormUtil.getController(PROVINCIA_CONTROLLER_NAME);
		try {
			provinciaController.clearCriteria();
			Criteria criteria = provinciaController.getCriteria();
			criteria.addNullExpression(provinciaController.getFieldName(IPayrollAlias.PROVINCIA_COMUNIDAD_CDG));
			provinciaController.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}		
	}
	
}
