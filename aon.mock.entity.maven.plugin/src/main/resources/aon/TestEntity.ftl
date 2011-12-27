// AON-TEST-ENTITY ${date}
package com.esferalia.aon.test;

import org.junit.Test;

import ${aonPackage}.${aonEntity};

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public class TestEntity${aonEntity} {

	@Test
	public void test() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(${aonEntity}.class);
		bean.getList(null);
	}

}
