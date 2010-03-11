package com.esferalia.aon.payroll.test;

import junit.framework.TestCase;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.payroll.ParteIT;


public class ParteITTest extends TestCase{

	public void testParteIT() {
		try {
			BeanManager.getManagerBean(ParteIT.class);
		} catch (ManagerBeanException e) {
			fail(e.getMessage());
		}
	}
}
