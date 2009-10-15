package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.finance.controller.FinancePaymentController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FinancePaymentControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			Expression pendingExpr = ExpressionUtilities.getEqualExpression(event.getController().getManagerBean().getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
			Expression returnedExpr = ExpressionUtilities.getEqualExpression(event.getController().getManagerBean().getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.RETURNED);
			criteria.addExpression(ExpressionUtilities.getOrExpression(pendingExpr, returnedExpr));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Cannot add criteria before model Initialized", e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		FinancePaymentController financePaymentController = (FinancePaymentController)event.getController();
		financePaymentController.init(((Finance)financePaymentController.getTo()).getTotalAmount());
	}

}