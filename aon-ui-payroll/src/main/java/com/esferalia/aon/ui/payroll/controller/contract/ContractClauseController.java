package com.esferalia.aon.ui.payroll.controller.contract;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;


public class ContractClauseController extends LinesController {
	
	private boolean enterpriseClause;

	public boolean isEnterpriseClause() {
		return enterpriseClause;
	}

	public void setEnterpriseClause(boolean enterpriseClause) {
		this.enterpriseClause = enterpriseClause;
	}
	
	@Override
	public void initializeModel() {
		if (isEnterpriseClause()) {
			try {
				getCriteria().addNullExpression("ContractClause.contract");
				super.initializeModel();
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("No se han podido cargar las cláusulas de contrato");
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e);
			}
		} else {
			try {
				getCriteria().addNotNullExpression("ContractClause.contract");
				super.initializeModel();
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("No se han podido cargar las cláusulas de contrato");
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e);
			}
		}
	}
	
	
}
