package com.code.aon.ui.employee.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ContractListListener extends ControllerAdapter {

	private WorkPlace workPlace;
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Expression expr1;
	    	Expression expr2;
			event.getController().getCriteria().addEqualExpression(getController().getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ID), getWorkPlace().getId());
			event.getController().getCriteria().addLessThanOrEqualExpression(getController().getFieldName(IEmployeeAlias.CONTRACT_START_DATE), new Date()); 
	    	expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getController().getFieldName(IEmployeeAlias.CONTRACT_END_DATE), new Date());
	    	expr2 = ExpressionUtilities.getNullExpression(getController().getFieldName(IEmployeeAlias.CONTRACT_END_DATE));
	    	event.getController().getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));	
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}