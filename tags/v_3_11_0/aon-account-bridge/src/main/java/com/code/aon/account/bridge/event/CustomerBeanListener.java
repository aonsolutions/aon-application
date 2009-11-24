package com.code.aon.account.bridge.event;

import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.customer.Customer;

public class CustomerBeanListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		Customer customer = (Customer)event.getTo();
		AccountUtil.obtainCustomerAccount(customer.getRegistry());
	}

}
