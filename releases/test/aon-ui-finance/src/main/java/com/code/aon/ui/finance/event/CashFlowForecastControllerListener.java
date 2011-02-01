package com.code.aon.ui.finance.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.finance.controller.CashFlowForecastController;
import com.code.aon.ui.finance.controller.CashFlowForecastParams;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CashFlowForecastControllerListener extends ControllerAdapter {
	
	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		CashFlowForecastController cf = (CashFlowForecastController) event.getController();
		cf.getParams().initialize();
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			CashFlowForecastController cf = (CashFlowForecastController) event.getController();
			Criteria c = cf.getCriteria();
			CashFlowForecastParams params = cf.getParams();
			if (params.getRegistryBank() != null && params.getRegistryBank().getId() != null) {
				c.addEqualExpression(cf.getFieldName(IFinanceAlias.CASH_FLOW_FORECAST_REGISTRY_BANK_ID), params.getRegistryBank().getId());
			}
			if (StringUtils.isNotEmpty(params.getDescription())) {
				c.addExpression(cf.getFieldName(IFinanceAlias.CASH_FLOW_FORECAST_DESCRIPTION), params.getDescription());
			}
			if (params.getAmount() != null) {
				c.addEqualExpression(cf.getFieldName(IFinanceAlias.CASH_FLOW_FORECAST_AMOUNT), params.getAmount());
			}
			if (params.getPaymentDay() != null) {
				c.addEqualExpression(cf.getFieldName(IFinanceAlias.CASH_FLOW_FORECAST_PAYMENT_DAY), params.getPaymentDay());
			}
			if (params.getStartDate() != null) {
				c.addGreaterThanOrEqualExpression(cf.getFieldName(IFinanceAlias.CASH_FLOW_FORECAST_START_DATE), params.getStartDate());
			}
			if (params.getDueDate() != null) {
				String alias = cf.getFieldName(IFinanceAlias.CASH_FLOW_FORECAST_DUE_DATE); 
				Expression e1 = ExpressionUtilities.getLessThanOrEqualExpression(alias, params.getDueDate());
				Expression e2 = ExpressionUtilities.getNullExpression(alias);
				c.addExpression(ExpressionUtilities.getOrExpression(e1, e2) );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error al realizar la búsqueda. [" + e.getMessage() + "]");
		} catch (ExpressionException e) {
			throw new ControllerListenerException("Error al realizar la búsqueda. [" + e.getMessage() + "]");
		}
	}
}
