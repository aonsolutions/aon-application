package com.code.aon.ui.finance.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class FinanceListSearchListener extends ControllerSearchListener {

	private FinanceStatus[] financeStatuses;

	public FinanceStatus[] getFinanceStatuses() {
		return financeStatuses;
	}

	public void setFinanceStatuses(FinanceStatus[] financeStatuses) {
		this.financeStatuses = financeStatuses;
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getFinanceStatuses())) {
			String status = getController().resolveAlias(IFinanceAlias.FINANCE_FINANCE_STATUS);
			addEnumToCriteria(criteria, status, getFinanceStatuses());
		}
	}

}