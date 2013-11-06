package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.finance.controller.FBatchController;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class FBatchSearchListener extends ControllerSearchListener {

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_BATCH_PAYMENT), ((FBatchController)getController()).isPayment());
		if (((FBatchController)getController()).isPayroll()) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_TYPE), FinanceBatchType.AEB_34_N);
		}
	}

}