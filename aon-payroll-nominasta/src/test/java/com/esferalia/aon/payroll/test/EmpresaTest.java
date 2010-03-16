package com.esferalia.aon.payroll.test;

import java.util.List;

import junit.framework.TestCase;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.Empresa;


public class EmpresaTest extends TestCase{

	public void testParteIT() throws Exception {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Empresa.class);
			List<ITransferObject> list = bean.getList( null,0,10 );
			if (list.size() == 0) {
				fail("No hay datos en Empresa");	
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
