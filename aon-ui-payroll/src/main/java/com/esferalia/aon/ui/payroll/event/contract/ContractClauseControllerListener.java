package com.esferalia.aon.ui.payroll.event.contract;


import javax.faces.event.AbortProcessingException;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContractClause;
import com.esferalia.aon.ui.payroll.controller.contract.ContractClauseController;
import com.esferalia.aon.ui.payroll.controller.contract.EnterpriseClauseController;

public class ContractClauseControllerListener extends ControllerAdapter{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		BasicController controller = (BasicController) event.getController();
		try {
			if (controller.getBeanName().equals("enterpriseClause")) {
				controller.getCriteria().addNullExpression("ContractClause.contract");
			} else {
				controller.getCriteria().addNotNullExpression("ContractClause.contract");
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se han podido cargar las cláusulas de contrato");
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) 
			throws ControllerListenerException {
		BasicController controller = (BasicController) event.getController();
		ContractClause clause = (ContractClause)controller.getTo();
		try {
			int line;
			if (controller.getBeanName().equals("enterpriseClause")) {
				clause.setGeneral(true);
				line = ((EnterpriseClauseController) event.getController()).calculateNextLine(clause.isGeneral());
			} else {
				line = ((ContractClauseController) event.getController()).calculateNextLine(clause.isGeneral());
			}
			clause.setLine(line);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}
	
}
