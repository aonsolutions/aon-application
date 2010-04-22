package com.esferalia.aon.payroll.test;

import junit.framework.TestCase;

import com.esferalia.aon.payroll.AonPayroll;
import com.esferalia.aon.payroll.core.empleado.EmpleadoDAOFactory;
import com.esferalia.aon.payroll.core.empleado.EmpleadoParams;
import com.esferalia.aon.payroll.core.empleado.IEmpleadoDAO;


public class EmpleadoTest extends TestCase{

	public void testEmpleado() throws Exception {
		try {
			AonPayroll.configure();
			IEmpleadoDAO dao = EmpleadoDAOFactory.getInstance().getEmpleadoDAO();
			EmpleadoParams params = new EmpleadoParams();
			params.setEmpresa("*ALAN*");
			dao.getEmpleados(params);
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
