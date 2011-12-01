package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class ContractDataSearchListener extends ControllerSearchListener {
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
//		if(getController()){
//			
//		}
		String alias = getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE);
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias, new Date());
		Expression expr2 = ExpressionUtilities.getNullExpression(alias);
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
//		alias = getFieldName(IPayrollAlias.CONTRACT_DATA_START_DATE);
//		criteria.addLessThanOrEqualExpression(alias, new Date());
		criteria.addOrder(alias, false);
		alias = getFieldName(IPayrollAlias.CONTRACT_DATA_NAME);
		criteria.addOrder(alias);
	}

}