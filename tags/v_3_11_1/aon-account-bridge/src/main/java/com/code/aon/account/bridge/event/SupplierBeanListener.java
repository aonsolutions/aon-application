package com.code.aon.account.bridge.event;

import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.supplier.Supplier;

public class SupplierBeanListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		Supplier supplier = (Supplier)event.getTo();
		AccountUtil.obtainSupplierAccount(supplier.getRegistry());
	}

}
