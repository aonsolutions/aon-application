package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.finance.controller.FBatchController;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class FBatchSearchListener extends ControllerSearchListener {

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IFinanceAlias.FINANCE_BATCH_PAYMENT), ((FBatchController)getController()).isPayment());
	}

}