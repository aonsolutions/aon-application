package com.esferalia.aon.payroll.test;

import junit.framework.TestCase;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.payroll.Persona;


public class ParteITTest extends TestCase{

	public void testParteIT() throws Exception {
		try {
			BeanManager.getManagerBean(Persona.class);
		} catch (ManagerBeanException e) {
			fail(e.getMessage());
		}
	}
}
