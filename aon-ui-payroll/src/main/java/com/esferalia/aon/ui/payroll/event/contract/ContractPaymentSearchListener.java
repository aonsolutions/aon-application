package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.ui.payroll.controller.contract.ContractPaymentController;

public class ContractPaymentSearchListener extends ControllerSearchListener {
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		ContractPaymentController controller = (ContractPaymentController) this.getController();
		String alias = null;
		if(controller.getHandler().isSearchCurrentVariables()){
			alias = getFieldName(IPayrollAlias.CONTRACT_PAYMENT_END_DATE);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias, new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(alias);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
			alias = getFieldName(IPayrollAlias.CONTRACT_PAYMENT_START_DATE);
			criteria.addLessThanOrEqualExpression(alias, new Date());
		}
		alias = getFieldName(IPayrollAlias.CONTRACT_PAYMENT_START_DATE);
		criteria.addOrder(alias, false);
		alias = getFieldName(IPayrollAlias.CONTRACT_PAYMENT_DESCRIPTION);
		criteria.addOrder(alias);
	}

}