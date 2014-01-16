package com.esferalia.aon.ui.payroll.event.contract;


import javax.faces.event.AbortProcessingException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContractClause;
import com.esferalia.aon.ui.payroll.controller.contract.ContractClauseController;

public class ContractClauseControllerListener extends ControllerAdapter{
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		ContractClauseController controller = (ContractClauseController) event.getController();
		try {
			if (controller.isEnterpriseClause()) {
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
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ContractClauseController controller = (ContractClauseController) event.getController();
		ContractClause clause = (ContractClause)controller.getTo();
		if(controller.isEnterpriseClause()){
			clause.setGeneral(true);
		}
		try {
			clause.setLine(controller.calculateNextLine(clause.isGeneral()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}	
}
