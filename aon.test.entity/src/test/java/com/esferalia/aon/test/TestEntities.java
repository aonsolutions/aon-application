package com.esferalia.aon.test;

import org.junit.Test;

import com.code.aon.accounting.AutoConcept;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;


public class TestEntities {

	@Test
	public void test() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AutoConcept.class);
		bean.getList(null);
	}

}
