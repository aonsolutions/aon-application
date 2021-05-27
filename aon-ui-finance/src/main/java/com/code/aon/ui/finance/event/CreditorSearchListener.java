package com.code.aon.ui.finance.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.registry.controller.event.RegistryPayMethodSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class CreditorSearchListener extends RegistryPayMethodSearchListener {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private CreditorStatus[] creditorStatuses;
	
	public CreditorStatus[] getCreditorStatuses() {
		return creditorStatuses;
	}

	public void setCreditorStatuses(CreditorStatus[] creditorStatuses) {
		this.creditorStatuses = creditorStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		CreditorStatus[] defaultCreditorStatus = {CreditorStatus.ACTIVE, CreditorStatus.BLOCKED};
		setCreditorStatuses(defaultCreditorStatus);
		super.init();
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getCreditorStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.CREDITOR_STATUS);
			addEnumToCriteria(criteria, status, getCreditorStatuses());
		}
		super.completeCriteria(criteria);
	}
	
}