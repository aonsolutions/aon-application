package com.code.aon.ui.employee.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.employee.controller.SalaryDraftController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class SalaryDraftDeductionLinesControllerListener extends LinesControllerListener{
	
	@Override
	protected void updateDetailCriteria(IController master, boolean reset) throws ControllerListenerException {
		try {
			super.updateDetailCriteria(master, reset);
			SalaryDraftController sc =  (SalaryDraftController) master;
			Date startDate = sc.getStartDate()==null?new Date():sc.getStartDate();
			Date endDate = sc.getEndDate()==null?new Date():sc.getEndDate();
			IController detail = getDetailController();
			detail.getCriteria().addLessThanOrEqualExpression(detail.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_START_DATE), startDate );
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(detail.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_END_DATE), endDate);
			Expression expr2 = ExpressionUtilities.getNullExpression(detail.getFieldName(IEmployeeAlias.CONTRACT_DEDUCTION_END_DATE));
			detail.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		} 
	}
    
}
