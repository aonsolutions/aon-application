package com.code.aon.account.bridge.event;

import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.finance.Creditor;

public class CreditorBeanListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		Creditor creditor = (Creditor)event.getTo();
		AccountUtil.obtainCreditorAccount(creditor.getRegistry());
	}

}
