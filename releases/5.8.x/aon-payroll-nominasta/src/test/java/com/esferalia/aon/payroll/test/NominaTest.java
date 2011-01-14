package com.esferalia.aon.payroll.test;

import java.util.List;

import junit.framework.TestCase;

import com.esferalia.aon.payroll.AonPayroll;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.nomina.INominaDAO;
import com.esferalia.aon.payroll.core.nomina.NominaDAOFactory;
import com.esferalia.aon.payroll.core.nomina.NominaParams;


public class NominaTest extends TestCase{

	public void testNomina() throws Exception {
		AonPayroll.configure();
		NominaDAOFactory factory = NominaDAOFactory.getInstance();
		INominaDAO nominaDAO = factory.getNominaDAO();
		
		NominaParams params = new NominaParams();
		params.setYear(2009);
		params.setMes(1);
		params.setOffset(0);
		params.setCount(500);
		List<INomina> list = nominaDAO.getNominas(params);
		for (INomina nomina :list) {
			System.out.println( nomina );
		}
	}
}
