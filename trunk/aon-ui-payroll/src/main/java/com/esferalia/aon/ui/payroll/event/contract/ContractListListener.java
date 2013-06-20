package com.esferalia.aon.ui.payroll.event.contract;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class ContractListListener extends ControllerAdapter {

	private WorkPlace workPlace;
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Expression expr1;
	    	Expression expr2;
			if(getWorkPlace()!=null){
				event.getController().getCriteria().addEqualExpression(getController().getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ID), getWorkPlace().getId());
			}
			event.getController().getCriteria().addLessThanOrEqualExpression(getController().getFieldName(IEntityAlias.CONTRACT_START_DATE), new Date()); 
	    	expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getController().getFieldName(IEntityAlias.CONTRACT_END_DATE), new Date());
	    	expr2 = ExpressionUtilities.getNullExpression(getController().getFieldName(IEntityAlias.CONTRACT_END_DATE));
	    	event.getController().getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));	
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}