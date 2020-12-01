package com.esferalia.aon.in.payroll.tgss.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

public class ReportTest {

	@Test
	public void test() throws IOException, UnknownPDFException {
		try ( InputStream is = ReportTest.class.getResourceAsStream("cccvidalaboral.pdf") ){
			ArrayList<Employee> employees = (ArrayList<Employee>) CCCVidaLaboral.parse(is,new Employee.EmployeeBuilder());
			for (Employee emp: employees) {
				System.out.println("--------------EMPLOYEE-------------");
				System.out.println(emp);
			}
		}
	}

}
