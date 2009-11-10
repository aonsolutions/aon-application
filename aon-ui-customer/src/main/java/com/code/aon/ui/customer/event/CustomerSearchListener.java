package com.code.aon.ui.customer.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;

public class CustomerSearchListener extends RegistrySearchListener {

	private CustomerStatus[] customerStatuses;
	
	public CustomerStatus[] getCustomerStatuses() {
		return customerStatuses;
	}

	public void setCustomerStatuses(CustomerStatus[] customerStatuses) {
		this.customerStatuses = customerStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		CustomerStatus[] defaultCustomerStatus = {CustomerStatus.ACTIVE};
		setCustomerStatuses(defaultCustomerStatus);
		super.init();
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (!ArrayUtils.isEmpty(getCustomerStatuses())) {
			String status = getController().resolveAlias(ICustomerAlias.CUSTOMER_STATUS);
			addEnumToCriteria(criteria, status, getCustomerStatuses());
		}
		super.completeCriteria();
	}
	
}