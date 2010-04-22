package com.esferalia.aon.payroll.test;

import java.util.List;

import junit.framework.TestCase;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.Nomina;


public class NominaTest extends TestCase{

	public void testNomina() throws Exception {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Nomina.class);
			List<ITransferObject> list = bean.getList( null,0,10 );
			if (list.size() == 0) {
				fail("No hay datos en Nominas");	
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
