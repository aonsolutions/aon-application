package com.code.aon.ui.finance.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class FinanceTrackingListSearchListener extends ControllerSearchListener {

	private RegistryBank registryBank;
	private FinanceTrackingType[] financeTrackingTypes;

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public FinanceTrackingType[] getFinanceTrackingTypes() {
		return financeTrackingTypes;
	}

	public void setFinanceTrackingTypes(FinanceTrackingType[] financeTrackingTypes) {
		this.financeTrackingTypes = financeTrackingTypes;
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addNullExpression(getFieldName(IFinanceAlias.FINANCE_TRACKING_BANK_STATEMENT_LINK));
		criteria.addEqualExpression(getFieldName(IFinanceAlias.FINANCE_TRACKING_RECORDED), new Boolean(false));
		if ((getRegistryBank() != null) && (getRegistryBank().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IFinanceAlias.FINANCE_TRACKING_REGISTRY_BANK_ID), getRegistryBank().getId());
		}
		if (!ArrayUtils.isEmpty(getFinanceTrackingTypes())) {
			String status = getController().resolveAlias(IFinanceAlias.FINANCE_TRACKING_TYPE);
			addEnumToCriteria(criteria, status, getFinanceTrackingTypes());
		}
	}

}