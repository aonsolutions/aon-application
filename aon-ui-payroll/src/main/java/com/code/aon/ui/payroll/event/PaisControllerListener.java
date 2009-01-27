package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.geograficas.Pais;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class PaisControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Pais p = (Pais)event.getController().getTo();
		if(p.getCdg().length()==0)
			p.setCdg("000");
		else if (p.getCdg().length()==1)
			p.setCdg("00"+p.getCdg());
		else if (p.getCdg().length()==2)
			p.setCdg("0"+p.getCdg());
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		resetComunidadModel();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		resetComunidadModel();
	}

	private void resetComunidadModel() throws ControllerListenerException {
		LinesController comunidadController = (LinesController) FormUtil.getController(IPayrollConstants.COMUNIDAD_CONTROLLER_NAME);
		try {
			comunidadController.clearCriteria();
			Criteria criteria = comunidadController.getCriteria();
			criteria.addNullExpression(comunidadController.getFieldName(IPayrollAlias.COMUNIDAD_PAIS_CDG));
			comunidadController.initializeModel();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );			
		}		
	}
	
}
