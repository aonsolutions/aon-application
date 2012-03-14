package com.code.aon.ui.customer.report.test;

import junit.framework.TestCase;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;


public class EntityTest  extends TestCase {
	
	public void testEntity() throws Exception {
		
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		bean.getList(c);
		
	}


}
