package com.code.aon.ui.accounting.event;

import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.report.StatementController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class StatementDetailListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IController c = event.getController();
			StatementController asc  = (StatementController) FormUtil
				.getController(IAccountingConstants.STATEMENT_CONTROLLER_NAME);
			SummaryProviderParameters params = asc.getParams();
			Criteria criteria = c.getCriteria();
			
			if (params.getPeriod() != null && params.getPeriod().getId() != null) {
				criteria.addEqualExpression(c.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD), params.getPeriod().getId());
			}
			String alias = c.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE);
			if (params.getFromDate() != null) {
				criteria.addGreaterThanOrEqualExpression(alias,params.getFromDate()); 
			}
			if (params.getToDate() != null) {
				criteria.addLessThanOrEqualExpression(alias,params.getToDate()); 
			}
			if (params.getSecurityLevel() != null) {
				criteria.addEqualExpression(c.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_SECURITY_LEVEL), params.getSecurityLevel());
			}
//			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(c.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE), AccountEntryType.OPENING));	
//			if (params.isExcludeClosingEntry()) {
//				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(c.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE), AccountEntryType.CLOSING));	
//			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

}
