package com.code.aon.ui.finance.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.registry.controller.event.RegistryPayMethodSearchListener;

public class CreditorSearchListener extends RegistryPayMethodSearchListener {

	private CreditorStatus[] creditorStatuses;
	
	public CreditorStatus[] getCreditorStatuses() {
		return creditorStatuses;
	}

	public void setCreditorStatuses(CreditorStatus[] creditorStatuses) {
		this.creditorStatuses = creditorStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		CreditorStatus[] defaultCreditorStatus = {CreditorStatus.ACTIVE};
		setCreditorStatuses(defaultCreditorStatus);
		super.init();
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (!ArrayUtils.isEmpty(getCreditorStatuses())) {
			String status = getController().resolveAlias(IFinanceAlias.CREDITOR_STATUS);
			addEnumToCriteria(criteria, status, getCreditorStatuses());
		}
		super.completeCriteria();
	}
	
}