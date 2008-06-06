package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.FinanceReturnController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FinanceReturnControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addEqualExpression(event.getController().getManagerBean().getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Cannot add criteria before model Initialized", e);
		}
	}
	
	@Override
	public void beforeBeanReset(ControllerEvent event) throws ControllerListenerException {
		FinanceReturnController financeReturnController = (FinanceReturnController)event.getController();
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeReturnController.getFieldName(IFinanceAlias.FINANCE_PAYMENT),financeReturnController.getPayment());
			if(financeReturnController.getRegistryId() != null){
				criteria.addEqualExpression(financeReturnController.getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID),financeReturnController.getRegistryId());
			}
			if(financeReturnController.getSeries() != null && !"".equals(financeReturnController.getSeries())){
				criteria.addEqualExpression(financeReturnController.getFieldName(IFinanceAlias.FINANCE_INVOICE_SERIES),financeReturnController.getSeries());
			}
			if(financeReturnController.getNumber() != null){
				criteria.addEqualExpression(financeReturnController.getFieldName(IFinanceAlias.FINANCE_INVOICE_NUMBER),financeReturnController.getNumber());
			}
			if(financeReturnController.getFromDate() != null){
				criteria.addGreaterThanOrEqualExpression(financeReturnController.getFieldName(IFinanceAlias.FINANCE_DUE_DATE),financeReturnController.getFromDate());
			}
			if(financeReturnController.getToDate() != null){
				criteria.addLessThanOrEqualExpression(financeReturnController.getFieldName(IFinanceAlias.FINANCE_DUE_DATE),financeReturnController.getToDate());
			}
			financeReturnController.setCriteria(criteria);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error creating criteria",e);
		}
	}
}