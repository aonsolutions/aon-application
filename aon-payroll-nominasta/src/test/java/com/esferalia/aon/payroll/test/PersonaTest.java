package com.esferalia.aon.payroll.test;

import java.util.List;

import junit.framework.TestCase;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.AonPayroll;
import com.esferalia.aon.payroll.Empresa;
import com.esferalia.aon.payroll.Persona;
import com.esferalia.aon.payroll.core.empleado.EmpleadoDAOFactory;
import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;
import com.esferalia.aon.payroll.core.empleado.IEmpleadoDAO;


public class PersonaTest extends TestCase{

	public void testEmpleado() throws Exception {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Persona.class);
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
