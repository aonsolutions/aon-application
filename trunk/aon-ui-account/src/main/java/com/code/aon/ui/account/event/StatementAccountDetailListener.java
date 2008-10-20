package com.code.aon.ui.account.event;

import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.account.summary.SummaryProviderParameters;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.account.controller.AccountStatementController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class StatementAccountDetailListener extends ControllerAdapter {

	private static final String ACCOUNT_STATEMENT_CONTROLLER_NAME = "accStatement";

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IController c = event.getController();
			AccountStatementController asc = (AccountStatementController) AonUtil
					.getRegisteredBean(ACCOUNT_STATEMENT_CONTROLLER_NAME);
			SummaryProviderParameters params = asc.getSummaryProviderParameters();
			Criteria criteria = c.getCriteria();
			
			if (params.getPeriod() != null && params.getPeriod().getId() != null) {
				criteria.addEqualExpression(c.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD), params.getPeriod().getId());
			}
			String alias = c.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE);
			if (params.getFromDate() != null) {
				criteria.addGreaterThanOrEqualExpression(alias,params.getFromDate()); 
			}
			if (params.getToDate() != null) {
				criteria.addLessThanOrEqualExpression(alias,params.getToDate()); 
			}
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(c.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE), AccountEntryType.OPENING));
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(c.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE), AccountEntryType.CLOSING));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

}
