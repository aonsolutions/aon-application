package com.esferalia.aon.payroll.test;

import java.util.List;

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
			List<IEmpleadoDAO> list = (List<IEmpleadoDAO>) dao.getEmpleados(params);
			System.out.println(list.size());
			for (IEmpleadoDAO empleado: list) {
				
			}
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
