package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
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
	public void beforeBeanReset(ControllerEvent event) throws ControllerListenerException {
		FinancePaymentController financePaymentController = (FinancePaymentController)event.getController();
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financePaymentController.getFieldName(IFinanceAlias.FINANCE_PAYMENT),financePaymentController.getPayment());
			if(financePaymentController.getRegistryId() != null){
				criteria.addEqualExpression(financePaymentController.getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID),financePaymentController.getRegistryId());
			}
			if(financePaymentController.getSeries() != null && !"".equals(financePaymentController.getSeries())){
				criteria.addEqualExpression(financePaymentController.getFieldName(IFinanceAlias.FINANCE_INVOICE_SERIES),financePaymentController.getSeries());
			}
			if(financePaymentController.getNumber() != null){
				criteria.addEqualExpression(financePaymentController.getFieldName(IFinanceAlias.FINANCE_INVOICE_NUMBER),financePaymentController.getNumber());
			}
			if(financePaymentController.getFromDate() != null){
				criteria.addGreaterThanOrEqualExpression(financePaymentController.getFieldName(IFinanceAlias.FINANCE_DUE_DATE),financePaymentController.getFromDate());
			}
			if(financePaymentController.getToDate() != null){
				criteria.addLessThanOrEqualExpression(financePaymentController.getFieldName(IFinanceAlias.FINANCE_DUE_DATE),financePaymentController.getToDate());
			}
			financePaymentController.setCriteria(criteria);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error creating criteria",e);
		}
	}
}